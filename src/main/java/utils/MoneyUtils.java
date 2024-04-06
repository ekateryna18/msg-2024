package utils;

import domain.AccountModel;
import domain.AccountType;
import domain.CurrencyType;
import domain.MoneyModel;

public class MoneyUtils {

    public static MoneyModel convert(MoneyModel money, CurrencyType toCurrency) {
        double conversionRate = getConversionRate(money.getCurrency(), toCurrency);
        double convertedAmount = money.getAmount() * conversionRate;
        return new MoneyModel(convertedAmount, toCurrency);
    }

    public static double getConversionRate(CurrencyType fromCurrency, CurrencyType toCurrency) {
        if (fromCurrency == CurrencyType.RON && toCurrency == CurrencyType.EUR) {
            return 0.2;
        }
        if (fromCurrency == CurrencyType.EUR && toCurrency == CurrencyType.RON) {
            return 4.98;
        }
        return 1.0;
    }

    public static void validateTransaction(AccountModel fromAccount, AccountModel toAccount, MoneyModel value){
        //if account is of SAVINGS type, it cant perform any transaction to either SAVINGS or CHECKING
        if(fromAccount.getAccountType() == AccountType.SAVINGS && (toAccount.getAccountType() == AccountType.SAVINGS || toAccount.getAccountType() == AccountType.CHECKING))
            throw new RuntimeException("Cannot transfer from " + fromAccount.getAccountType() + " to "+ toAccount.getAccountType());
        //if there is not enough money for the transaction, it can't be made
        if(fromAccount.getBalance().getAmount() - value.getAmount() < 0)
            throw new RuntimeException("Not enough money into the account for the transaction");
    }
}
