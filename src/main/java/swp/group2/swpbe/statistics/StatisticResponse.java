package swp.group2.swpbe.statistics;

public class StatisticResponse {
    private int totalUser;
    private int users;
    private int expert;
    private int totalDocument;
    private int totalFlashcard;
    private double totalIncome;
    public StatisticResponse(int totalUser, int users, int expert, int totalDocument, int totalFlashcard,
            double totalIncome) {
        this.totalUser = totalUser;
        this.users = users;
        this.expert = expert;
        this.totalDocument = totalDocument;
        this.totalFlashcard = totalFlashcard;
        this.totalIncome = totalIncome;
    }
    public StatisticResponse() {
    }
    public int getTotalUser() {
        return totalUser;
    }
    public void setTotalUser(int totalUser) {
        this.totalUser = totalUser;
    }
    public int getUsers() {
        return users;
    }
    public void setUsers(int users) {
        this.users = users;
    }
    public int getExpert() {
        return expert;
    }
    public void setExpert(int expert) {
        this.expert = expert;
    }
    public int getTotalDocument() {
        return totalDocument;
    }
    public void setTotalDocument(int totalDocument) {
        this.totalDocument = totalDocument;
    }
    public int getTotalFlashcard() {
        return totalFlashcard;
    }
    public void setTotalFlashcard(int totalFlashcard) {
        this.totalFlashcard = totalFlashcard;
    }
    public double getTotalIncome() {
        return totalIncome;
    }
    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }
    @Override
    public String toString() {
        return "StatisticResponse [totalUser=" + totalUser + ", users=" + users + ", expert=" + expert
                + ", totalDocument=" + totalDocument + ", totalFlashcard=" + totalFlashcard + ", totalIncome="
                + totalIncome + "]";
    }

}
