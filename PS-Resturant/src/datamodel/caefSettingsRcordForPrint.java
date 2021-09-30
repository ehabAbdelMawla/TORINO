package datamodel;

import java.util.List;

public class caefSettingsRcordForPrint {

    public String categName;
    public List<statisticsRecordForPrint> productDetails;

    public caefSettingsRcordForPrint(String categName, List<statisticsRecordForPrint> productDetails) {
        this.categName = categName;
        this.productDetails = productDetails;
    }

    public String getCategName() {
        return categName;
    }

    public void setCategName(String categName) {
        this.categName = categName;
    }

    public List<statisticsRecordForPrint> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<statisticsRecordForPrint> productDetails) {
        this.productDetails = productDetails;
    }

}
