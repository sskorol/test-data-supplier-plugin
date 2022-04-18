# Test Data Supplier
[![IntelliJ IDEA Plugin](https://img.shields.io/badge/Download-latest-brightgreen.svg)](https://plugins.jetbrains.com/plugin/9868-test-data-supplier-plugin)

IntelliJ IDEA integration for [Test Data Supplier](https://github.com/sskorol/test-data-supplier) library.

Currently, plugin provides very simple inspections support to determine methods annotated with **DataSupplier** (DS) annotation, so that you could easily track missing DS errors and avoid unused methods' warnings.

Available features:
- Missing DS will be marked red for **Test** and **Factory** annotations.
- Clicking **dataProvider** arg value will trigger navigation to corresponding DS and vice versa.
- Clicking test name within **include** tag of TestNG xml or test class will trigger cross navigation as well.

Note that to avoid clashes with integrated **TestNG** plugin you have to disable native **DataProvider** inspections:

![ds-settings](https://user-images.githubusercontent.com/6638780/163784461-9f60dd23-470c-47e3-9631-1392f5e518db.gif)

After plugin installation you'll be able to see the following inspections and navigation options:

![ds-nav](https://user-images.githubusercontent.com/6638780/163788225-30a5c91d-def1-4ed4-8a17-2ff90776b045.gif)
