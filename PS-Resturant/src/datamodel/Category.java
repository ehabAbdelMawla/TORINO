package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Category extends RecursiveTreeObject<Arguments> {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
