package services;

import domain.CapitalizationFrequency;
import domain.SavingsAccountModel;
import repository.AccountsRepository;

import java.time.LocalDate;
import java.util.List;

public class SavingsManagerService {
    private LocalDate systemDate = LocalDate.now();

    public void passTime() {
        List<SavingsAccountModel> savingAccounts = AccountsRepository.INSTANCE.getAll().stream()
                .filter(account -> account instanceof SavingsAccountModel)
                .map(account -> (SavingsAccountModel) account).toList();

        LocalDate nextSystemDate = systemDate.plusMonths(1);

        savingAccounts.forEach(savingAccount -> {
            if (savingAccount.getInterestFrequency() == CapitalizationFrequency.MONTHLY) {
                addInterestByFrequency(savingAccount, nextSystemDate,1);
            }
            else if(savingAccount.getInterestFrequency() == CapitalizationFrequency.QUARTERLY){
                addInterestByFrequency(savingAccount, nextSystemDate,3);
            }
        });

        systemDate = nextSystemDate;
    }
    /***
     * generic function to add interest depending on the frequency
     */
    private void addInterestByFrequency(SavingsAccountModel savingAccount, LocalDate currentInterestMonth,int months){
        LocalDate nextInterestDateForAccount = savingAccount.getLastInterestAppliedDate().plusMonths(months);

        if (isSameMonthAndYear(currentInterestMonth, nextInterestDateForAccount)) {
            addInterest(savingAccount);
            savingAccount.setLastInterestAppliedDate(currentInterestMonth);
        }
    }
    private void addInterest(SavingsAccountModel savingAccount) {
        double interest = savingAccount.getBalance().getAmount() * savingAccount.getInterest();
        savingAccount.getBalance().setAmount(savingAccount.getBalance().getAmount() + interest);
    }

    private boolean isSameMonthAndYear(LocalDate date1, LocalDate date2) {
        return date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear();
    }
}