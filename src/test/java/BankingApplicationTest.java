import domain.CurrencyType;
import domain.MoneyModel;
import domain.TransactionModel;
import org.junit.BeforeClass;
import org.junit.Test;
import seed.SeedInitializer;
import services.SavingsManagerService;
import services.TransactionManagerService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seed.AccountsSeedData.*;
import static utils.MoneyUtils.convert;

public class BankingApplicationTest {
     //SeedInitializer.seedData();

    private static TransactionManagerService transactionManagerServiceInstance;
    private static SavingsManagerService savingsManagerServiceInstance;
    @BeforeClass
    public static void setUp(){
        SeedInitializer.seedData();
        transactionManagerServiceInstance = new TransactionManagerService();
        savingsManagerServiceInstance = new SavingsManagerService();
    }

    /**
     * Testing when a transaction is made in between accounts of different currency
     */
    @Test
    public void differentCurrencyTest() {
        double initialB = checkingAccountB.getBalance().getAmount();
        double initialC = checkingAccountC.getBalance().getAmount();
        MoneyModel money1 = new MoneyModel(50, CurrencyType.RON);
        MoneyModel convertedBalance = convert(money1, checkingAccountC.getBalance().getCurrency());

        TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                checkingAccountB.getId(),
                checkingAccountC.getId(),
                money1
        );
        assertEquals(transactionManagerServiceInstance.checkFunds(checkingAccountB.getId()).getAmount(), initialB-money1.getAmount(), 0.0);
        assertEquals(transactionManagerServiceInstance.checkFunds(checkingAccountC.getId()).getAmount(), initialC+convertedBalance.getAmount(), 0.0);
    }

    /**
     * Testing withdrawing money from an account
     */
    @Test
    public void withdrawalTest(){
        double initialB = checkingAccountB.getBalance().getAmount();
        MoneyModel money1 = new MoneyModel(50, CurrencyType.RON);
        TransactionModel transaction1 = transactionManagerServiceInstance.withdraw(
                checkingAccountB.getId(),
                money1
        );
        assertEquals(transactionManagerServiceInstance.checkFunds(checkingAccountB.getId()).getAmount(), initialB-money1.getAmount(), 0.0);
    }
}
