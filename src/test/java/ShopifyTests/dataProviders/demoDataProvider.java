package ShopifyTests.dataProviders;
import org.testng.annotations.DataProvider;


public class demoDataProvider {

  @DataProvider(name = "procentage")
  public static Object[][] procentage() {
    return new Object[][]{
        {"testing that on manitobah site 25% discount was applyed for from Tipi shoes ",
            "https://www.manitobah.com/collections/mens-moccasins", "Tipi", 25.00},
        {"testing that on manitobah site 9.375% discount was applyed for from Métis Moccasin shoes  ",
            "https://www.manitobah.com/collections/mens-moccasins", "Métis Moccasin", 9.375},

        {"testing that on uppercasemagazine site 6.94..% discount was applyed for from Encyclopedia Bundle  ",
            "https://uppercasemagazine.com/collections/books", "Encyclopedia Bundle", 6.944444444444444},
        {"testing that on uppercasemagazine site 9.72..% discount was applyed for from Little U Bundle: Volumes 1, 2, 3 shoes  ",
            "https://uppercasemagazine.com/collections/books", "Little U Bundle: Volumes 1, 2, 3", 9.722222222222222}
    };
  }

  @DataProvider(name = "checkoutItems")
  public static Object[][] checkoutItems() {
    return new Object[][]{
        {"testing that Tipi and checkout price is calculated properly ", "https://www.manitobah.com/collections/mens-moccasins", "Tipi", 25.00},
        {"testing that Métis Moccasin and checkout price is calculated properly ", "https://www.manitobah.com/collections/mens-moccasins", "Métis Moccasin", 9.375},

        {"testing that Encyclopedia Bundle checkout price is calculated properly with shipping price ",
            "https://uppercasemagazine.com/collections/books", "Encyclopedia Bundle", 6.944444444444444},
        {"testing that Ceramics checkout price is calculated properly with shipping price  ",
            "https://uppercasemagazine.com/collections/books", "Ceramics", 9.722222222222222}
    };
  }
}
