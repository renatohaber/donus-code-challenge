package br.com.donus.account.utils;

import org.springframework.stereotype.Component;

@Component
public class DocumentValidator {

    // https://www.vivaolinux.com.br/script/Codigo-para-validar-CPF-e-CNPJ-otimizado
    // By Carlos Caldas

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice = str.length() - 1, digito; indice >= 0; indice--) {
            digito = Integer.parseInt(str.substring(indice, indice + 1));
            soma += digito * peso[peso.length - str.length() + indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

    public boolean isValidCPF(String cpf) {
        if ((cpf == null) || (cpf.length() != 11)) return false;
        int digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF);
        int digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0, 9) + Integer.toString(digito1) + Integer.toString(digito2));
    }

    public boolean isValidCNPJ(String cnpj) {
        if ((cnpj == null) || (cnpj.length() != 14)) return false;
        int digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ);
        int digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0, 12) + Integer.toString(digito1) + Integer.toString(digito2));
    }
}