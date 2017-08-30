package com.conference.company;

import com.conference.Product;

public class Stall extends Booth {
    private Product[] products;

    public Stall(String companyId, String name, String type, Product[] products) {
        super(companyId, name, "Stall");
    }

}
