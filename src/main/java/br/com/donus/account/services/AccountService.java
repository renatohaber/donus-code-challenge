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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class AccountService {

    private static final TimeZone SAO_PAULO_TIME_ZONE = TimeZone.getTimeZone("America/Sao_Paulo");

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

        log.info("Received request for account creation: " + request);

        String formatted = formatDocument(request.getTaxId());
        if (!documentValidator.isValidCPF(formatted)) {
            throw new InvalidTaxIdException(request.getTaxId());
        }

        Optional<Account> current = accountRepository.findAccountByTaxId(formatted);
        current.ifPresent(c -> {
            throw new AccountAlreadyExistsException(request.getTaxId());
        });

        Account savedAccount = createInsertAccount(request, formatted);
        return accountMapper.sourceToTarget(savedAccount, SAO_PAULO_TIME_ZONE);
    }

    private String formatDocument(String taxId) {
        String document = taxId.replaceAll("[^\\d]", "");
        return StringUtils.leftPad(document, 11, "0");
    }

    private Account createInsertAccount(CreateAccountRequest request, String formatedTaxId) {

        AccountRequest accountRequest = AccountRequest.builder()
                .name(request.getName())
                .taxId(formatedTaxId)
                .balance(BigDecimal.ZERO)
                .build();

        Account account = accountMapper.targetToSource(accountRequest);
        return accountRepository.save(account);

    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public AccountResponse findByTaxId(String taxid) {

        log.info("Finding account by tax id: " + taxid);

        String formatted = formatDocument(taxid);
        return accountMapper.sourceToTarget(accountRepository.findAccountByTaxId(formatted)
                .orElseThrow(() -> new AccountNotFoundException(taxid)), SAO_PAULO_TIME_ZONE);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public AccountResponse findAccountById(UUID id) {

        log.info("Finding account by id: " + id);

        return accountMapper.sourceToTarget(accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id)), SAO_PAULO_TIME_ZONE);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PageResponse<AccountResponse> listAll(Pageable pageable) {

        log.info("Listing all account: " + pageable);

        Page<Account> accountPage = accountRepository.findAll(pageable);

        return pageMapper.sourceToTarget(accountPage.map(account -> accountMapper.sourceToListTarget(account, SAO_PAULO_TIME_ZONE)));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deposit(UUID id, BigDecimal value) {

        log.info(String.format("Deposit %s to account %s", value, id));

        if (value.compareTo(BigDecimal.ZERO)<0) {
            throw new NegativeValueException(value);
        }
        Account currentAccount = accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        BigDecimal bonus = value.multiply(BigDecimal.valueOf(0.005));
        BigDecimal deposit = value.add(bonus);
        currentAccount.setBalance(currentAccount.getBalance().add(deposit));

        registerTransaction(currentAccount.getId(), TransactionType.DEPOSIT, value);
        registerTransaction(currentAccount.getId(), TransactionType.BONUS, bonus);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void withdraw(UUID id, BigDecimal value) {

        log.info(String.format("Withdraw %s from account %s", value, id));

        if (value.compareTo(BigDecimal.ZERO)<0) {
            throw new NegativeValueException(value);
        }
        Account currentAccount = accountRepository.findAccountById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        BigDecimal tax = value.multiply(BigDecimal.valueOf(0.01));
        BigDecimal withdraw = value.add(tax);
        if (currentAccount.getBalance().compareTo(withdraw) > 0) {

            currentAccount.setBalance(currentAccount.getBalance().subtract(withdraw));

            registerTransaction(currentAccount.getId(), TransactionType.WITHDRAW, value);
            registerTransaction(currentAccount.getId(), TransactionType.TAX, tax);

        } else {
            throw new NotEnoughFundsException(withdraw);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void transfer(UUID id, UUID target, BigDecimal value) {

        log.info(String.format("Transfer %s from account %s to account %S", value, id, target));

        if (value.compareTo(BigDecimal.ZERO)<0) {
            throw new NegativeValueException(value);
        }
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

    @Transactional(propagation = Propagation.REQUIRED)
    private void registerTransaction(UUID accountId, TransactionType type, BigDecimal value) {

        Transaction tr = new Transaction();
        tr.setAccountId(accountId);
        tr.setTransactionType(type);
        tr.setAmount(value);

        log.info("Saving transaction to database: " + tr);

        this.transactionRepository.save(tr);
    }

}
