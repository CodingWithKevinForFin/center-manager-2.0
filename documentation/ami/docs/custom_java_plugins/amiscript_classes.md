# AMIScript: Custom Classes

### Overview

AMI script is an object oriented language where objects can be declared and there methods executed. It is possible to write your own classes in java and make them accessible via AmiScript.

The class must have the com.f1.ami.amicommon.customobjects.AmiScriptAccessible annotation. Constructors and methods that should be accessible via AmiScript must also be annotated with AmiScriptAccessible. Note the annotation allows for overriding the *name* (and *params* for methods and constructors).

### Properties

Use the first property to make the custom java objects available in amiweb and the second property to make them available in amicenter/amidb.

```
ami.web.amiscript.custom.classes=comma_delimited_list_of_fully_qualified_java_class_names
ami.center.amiscript.custom.classes=comma_delimited_list_of_fully_qualified_java_class_names
```

### Example

Java Code:

``` java
package com.demo;
import com.f1.ami.amicommon.customobjects.AmiScriptAccessible;

@AmiScriptAccessible(name = "TestAccount")
public class TestClass {
        private double price;
        private int quantity;
        private String name;

        @AmiScriptAccessible
        public TestClass(String name) { this.name = name; }

        @AmiScriptAccessible(name = "setValue", params = { "px", "qty" })
        public void setValue(double price, int quantity) {     
this.price = price;
this.quantity = quantity;
}

        @AmiScriptAccessible(name = "print")
        public String print() {
                return quantity + "@" + price + " for " + name + " is " + (quantity * price);
        }
}
```

Configuration:

```
ami.web.amiscript.custom.classes=com.demo.TestClass
ami.center.amiscript.custom.classes=com.demo.TestClass
```

AmiScript:

```amiscript
TestAccount myAccount=new TestAccount("ABC");
myAccount.setValue(40.5,1000);
session.log(myAccount.print());
```

