package br.com.donus.account.services;

import br.com.donus.account.data.dto.account.AccountRequest;
import br.com.donus.account.data.dto.account.AccountResponse;
import br.com.donus.account.data.dto.account.CreateAccountRequest;
import br.com.donus.account.data.dto.common.PageResponse;
import br.com.donus.account.data.entities.Account;
import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.data.entities.TransactionType;
import br.com.donus.account.data.mappers.AccountMapper;
import br.com.donus.account.data.mappers.common.PageMapper;
import br.com.donus.account.data.repositories.AccountRepository;
import br.com.donus.account.data.repositories.TransactionRepository;
import br.com.donus.account.exception.*;
import br.com.donus.account.utils.DocumentValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@Service
public class AccountService {

    private static final TimeZone SAO_PAULO_TIME_ZONE = TimeZone.getTimeZone("America/Sao_Paulo");

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final DocumentValidator documentValidator;
    private final AccountMapper accountMapper;
    private final PageMapper pageMapper;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository, DocumentValidator documentValidator, AccountMapper accountMapper, PageMapper pageMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.documentValidator = documentValidator;
        this.accountMapper = accountMapper;
        this.pageMapper = pageMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AccountResponse createAccount(CreateAccountRequest request) {

        if (!documentValidator.isValidCPF(request.getTaxId())) {
            throw new InvalidTaxIdException(request.getTaxId());
        }

        Optional<Account> current = accountRepository.findAccountByTaxId(request.getTaxId());
        current.ifPresent(c -> {
            throw new AccountAlreadyExistsException(request.getTaxId());
        });

        Account savedAccount = createInsertAccount(request);
        return accountMapper.sourceToTarget(savedAccount, SAO_PAULO_TIME_ZONE);
    }

    private Account createInsertAccount(CreateAccountRequest request) {

        String document = request.getTaxId().replaceAll("[^\\d.]", "");

        AccountRequest accountRequest = AccountRequest.builder()
                .name(request.getName())
                .taxId(document)
                .balance(BigDecimal.ZERO)
                .build();

        Account account = accountMapper.targetToSource(accountRequest);
        return accountRepository.save(account);

    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public AccountResponse findByTaxId(String taxid) {
        return accountMapper.sourceToTarget(accountRepository.findAccountByTaxId(taxid)
                .orElseThrow(() -> new AccountNotFoundException(taxid)), SAO_PAULO_TIME_ZONE);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public AccountResponse findAccountById(UUID id) {
        return accountMapper.sourceToTarget(accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id)), SAO_PAULO_TIME_ZONE);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResponse<AccountResponse> listAll(Pageable pageable) {
        Page<Account> accountPage = accountRepository.findAll(pageable);

        return pageMapper.sourceToTarget(accountPage.map(partner -> accountMapper.sourceToListTarget(partner, SAO_PAULO_TIME_ZONE)));

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deposit(UUID id, BigDecimal value) {

        Account currentAccount = accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        BigDecimal credit = value.multiply(BigDecimal.valueOf(1.005));
        currentAccount.setBalance(currentAccount.getBalance().add(credit));
        registerTransaction(currentAccount.getId(), TransactionType.DEPOSIT, value);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void withdraw(UUID id, BigDecimal value) {

        Account currentAccount = accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        BigDecimal tax = value.multiply(BigDecimal.valueOf(0.01));
        BigDecimal withdraw = value.add(tax);
        if (currentAccount.getBalance().compareTo(withdraw) > 0) {
            currentAccount.setBalance(currentAccount.getBalance().subtract(withdraw));
            registerTransaction(currentAccount.getId(), TransactionType.WITHDRAW, value);
        } else {
            throw new NotEnoughFundsException(withdraw);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void transfer(UUID id, UUID target, BigDecimal value) {

        Account currentAccount = accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        Account targetAccount = accountRepository.findAccountById(target)
                .orElseThrow(() -> new TargetAccountNotFoundException(target));

        if (currentAccount.getBalance().compareTo(value) > 0) {
            currentAccount.setBalance(currentAccount.getBalance().subtract(value));
            targetAccount.setBalance(targetAccount.getBalance().add(value));
            registerTransaction(currentAccount.getId(), TransactionType.TRANSFER_SEND, value);
            registerTransaction(targetAccount.getId(), TransactionType.TRANSFER_RECEIVED, value);
        } else {
            throw new NotEnoughFundsException(value);
        }
    }

    private void registerTransaction(UUID accountId, TransactionType type, BigDecimal value) {
        Transaction tr = new Transaction();
        tr.setAccountId(accountId);
        tr.setTransactionType(type);
        tr.setAmount(value);
        this.transactionRepository.save(tr);
    }

}
