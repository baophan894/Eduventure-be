package swp.group2.swpbe.course.response;

import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.course.entites.Course;

public class ExpertCourseResponse extends Course {
    private int totalUserBuy;
    private double income;

    public ExpertCourseResponse() {
    }

    public ExpertCourseResponse(int totalUserBuy, double income, String expertId, Double price, String description,
            String banner_url, String name, String topicId, ResourceStatus state) {
        super(expertId, price, description, banner_url, name, topicId, state);
        this.totalUserBuy = totalUserBuy;
        this.income = income;
    }

    public int getTotalUserBuy() {
        return this.totalUserBuy;
    }

    public void setTotalUserBuy(int totalUserBuy) {
        this.totalUserBuy = totalUserBuy;
    }

    public double getIncome() {
        return this.income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

}
