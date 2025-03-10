package swp.group2.swpbe.document.dto;

public class NewReview {
    private String review;


    public NewReview() {
    }

    public NewReview(String review) {
        this.review = review;
    }


    public String getReview() {
        return this.review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}
