# Test Data Supplier
IntelliJ IDEA integration for [Test Data Supplier](https://github.com/sskorol/test-data-supplier) library.

Currently plugin provides very simple inspections support to determine methods annotated with **DataSupplier** (DS) annotation, so that you could easily track missing DS errors, and avoid unused methods' warnings.

Apart from that, you'll be able to navigate to specified **dataProvider** in **Test** annotation.

Future releases may include more features, including duplicates checks and bi-directional navigation.

Note that to avoid clashes with integrated TestNG plugin, you have to disable common DataProvider inspections:

![image](https://user-images.githubusercontent.com/6638780/28659256-80fc5222-72b7-11e7-83bf-896b205c0527.png)
