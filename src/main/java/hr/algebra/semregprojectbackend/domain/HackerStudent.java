package hr.algebra.semregprojectbackend.domain;

import java.io.Serializable;

public class HackerStudent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String payload = "Some malicious payload";

    @Override
    public String toString() {
        return "HackerStudent with payload: " + payload;
    }
}
