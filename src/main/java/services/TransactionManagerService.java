package services;

import domain.AccountModel;
import domain.MoneyModel;
import domain.TransactionModel;
import repository.AccountsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static utils.MoneyUtils.convert;
import static utils.MoneyUtils.validateTransaction;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }
        //throws exception in case the transaction is not valid
        validateTransaction(fromAccount, toAccount, value);
        //if currencies are the same, the value doesn't get affected since it multiplies by 1
        MoneyModel convertedValue = convert(value, toAccount.getBalance().getCurrency());
        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                convertedValue,
                LocalDate.now()
        );

        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
        fromAccount.getTransactions().add(transaction);
        //if it is not withdrawal, we add the money to the receiving account
        if(!fromAccountId.equals(toAccountId))
        {
            toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + convertedValue.getAmount());
            toAccount.getTransactions().add(transaction);
        }
        return transaction;
    }

    public TransactionModel withdraw(String accountId, MoneyModel amount) {
        return  transfer(accountId, accountId, amount);
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }
}

