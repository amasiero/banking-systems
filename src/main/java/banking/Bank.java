package banking;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Bank implementation.
 */
public class Bank implements BankInterface {
    private LinkedHashMap<Long, Account> accounts;

    public Bank() {
        accounts = new LinkedHashMap<>();
    }

    private Account getAccount(Long accountNumber) {
        if (!accounts.keySet().contains(accountNumber)) {
            return null;
        }
        return accounts.get(accountNumber);
    }

    public Long openCommercialAccount(Company company, int pin, double startingDeposit) {
        Long idNumber = accounts.size() + 1L;
        accounts.put(idNumber, new CommercialAccount(company, idNumber, pin, startingDeposit));
        return idNumber;
    }

    public Long openConsumerAccount(Person person, int pin, double startingDeposit) {
        Long idNumber = accounts.size() + 1L;
        accounts.put(idNumber, new ConsumerAccount(person, idNumber, pin, startingDeposit));
        return idNumber;
    }

    public double getBalance(Long accountNumber) {
        if (!accounts.keySet().contains(accountNumber)) {
            return -1.0;
        }
        return accounts.get(accountNumber).getBalance();
    }

    public void credit(Long accountNumber, double amount) {
        if (!accounts.keySet().contains(accountNumber)) {
            return;
        }
        accounts.get(accountNumber).creditAccount(amount);
    }

    public boolean debit(Long accountNumber, double amount) {
        if (!accounts.keySet().contains(accountNumber)) {
            return false;
        }
        return accounts.get(accountNumber).debitAccount(amount);
    }

    public boolean authenticateUser(Long accountNumber, int pin) {
        if (!accounts.keySet().contains(accountNumber)) {
            return false;
        }
        return accounts.get(accountNumber).validatePin(pin);
    }
    
    public void addAuthorizedUser(Long accountNumber, Person authorizedPerson) {
        if (!accounts.keySet().contains(accountNumber) || authorizedPerson == null) {
            return;
        }
        Account account = accounts.get(accountNumber);
        if (account instanceof CommercialAccount) {
            ((CommercialAccount) account).addAuthorizedUser(authorizedPerson);
        }
    }

    public boolean checkAuthorizedUser(Long accountNumber, Person authorizedPerson) {
        if (!accounts.keySet().contains(accountNumber) || authorizedPerson == null) {
            return false;
        }
        Account account = accounts.get(accountNumber);
        if (account instanceof CommercialAccount) {
            return ((CommercialAccount) account).isAuthorizedUser(authorizedPerson);
        }
        return false;
    }

    public Map<String, Double> getAverageBalanceReport() {
        Map<String, Double> report = new HashMap<>();
        report.put("ConsumerAccount", getAverage("ConsumerAccount"));
        report.put("CommercialAccount", getAverage("CommercialAccount"));
        return report;
    }

    private Double getAverage(String className) {
        final AtomicInteger count = new AtomicInteger();
        return accounts.values()
                .stream()
                .filter(account -> account.getClass().getSimpleName().equals(className))
                .map(Account::getBalance)
                .reduce(0.0, (x, y) -> {
                    int number = count.incrementAndGet();
                    return (x * (number - 1) + y) / number;
                });
    }
}
