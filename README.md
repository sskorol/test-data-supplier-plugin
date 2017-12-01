# Test Data Supplier
[![IntelliJ IDEA Plugin](https://img.shields.io/badge/Download-latest-brightgreen.svg)](https://plugins.jetbrains.com/plugin/9868-test-data-supplier-plugin)

IntelliJ IDEA integration for [Test Data Supplier](https://github.com/sskorol/test-data-supplier) library.

Currently plugin provides very simple inspections support to determine methods annotated with **DataSupplier** (DS) annotation, so that you could easily track missing DS errors, and avoid unused methods' warnings.

Apart from that, you'll be able to navigate to specified **dataProvider** in **Test** and **Factory** annotations.

Future releases may include more features: duplicates checks and bi-directional navigation.

Note that to avoid clashes with integrated TestNG plugin, you have to disable common DataProvider inspections:

![image](https://user-images.githubusercontent.com/6638780/28659256-80fc5222-72b7-11e7-83bf-896b205c0527.png)

After plugin installation, you'll be able to see the following inspections:

![image](https://user-images.githubusercontent.com/6638780/28659976-eaa2f79c-72b9-11e7-802a-17d34fbfdb98.png)

 - Missing DS will be marked red for **Test** and **Factory** annotations.
 - Clicking **dataProvider** arg value will trigger navigation to corresponding DS.
