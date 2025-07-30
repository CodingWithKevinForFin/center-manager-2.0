# AMIScript: Scripting

## Variables

### Basic Types

```amiscript
Boolean b2 = true;
Byte b1 = 5b;

Integer i = 1; // also Int
Short s = 5s;
Long l = 5l;
BigInteger bi = "100";

Float f = 0.5f;
Double d = 0.5d;
BigDecimal bd = "0.5";

Number n = 100; // parent class for other numbers

String s = "s";
Char c = 'c';
UTC u1 = 1709118835000; // millis since epoch
UTCN u2 = 1709118835000000000; // nanos since epoch
```

### Basic Data Structures

```amiscript
List l = new List("a", "b", "c");
Set s = new Set("id001", "id002");
Map m = new Map("age", 23, "name", "alice");

List names = new List("alice", "bob", "charlie");
List ages = new List(23, 58, 31);
Map columnMap = new Map("names", names, "ages", ages);
Table t = new Table("myTable", columnMap);
```

### Basic Data Structures with Python Syntax

``` amiscript
List l = ["a", "b", "c"];
Map m = {"Americas":["New York","Toronto","Florida"], "Europe":"London", "Asia":["Singapore","Tokyo","Hong Kong"]};
// maps can also be declared with multi-line syntax
Map m2 = {
  "Americas":["New York","Toronto","Florida"], 
  "Europe":"London", 
  "Asia":["Singapore","Tokyo","Hong Kong"]
};
```

### Casting

```amiscript
Int i = 418;
String s = i; // implicit
Int i1 = (int) s; // explicit
Int i2 = (int) (s.substring(0,2)); // explicit
UTC u = parseDate("2024-02-28", "yyyy-MM-dd", "UTC"); // parsing
Map m = parseJson("{\"name\": \"alice\", \"age\": 23}"); // parsing
```

### Strings

```amiscript
String s1 = "hello \"world\"!"; // hello "world"!
String s2 = "first line: ${s1}"; // first line: hello "world"!
String s3 = """hello ${s1} \"world\"!"""; // hello ${s1} \"world\"!
String s4 = """hello
world""";
```

## Comments

```amiscript
// Single line
/*
Multi-line
*/
```

## If statements

```amiscript
int x = 6;
int y;
if (x > 5) {
    y = x+1;
    logInfo("true case");
} else {
    y = 0;
}
```

```amiscript
int x = 6;
int y;
if (x > 5)
    y=x+1;
```

```amiscript
int x = 6;
int y = x > 5 ? x+1 : 0;
```

## Loops

```amiscript
int x = 0;
for (int i=0; i<10; i++) {
  x += i;
}
```

```amiscript
int x = 0;
int i = 0;
while (i<10) {
  x += i;
  i++;
}
```

```amiscript
List myList = new List("Hello","World");
for (String s: myList) {
  logInfo(s);
}
```

```amiscript
int x = 0;
int i = 1;
while (true) {
  x += i;
  if (x < 5) {
    continue;
  }
  i++;
  if (i > 10) {
    break;
  }
}
```

## Methods

```amiscript
int addFive(int x) {
  return x + 5;
};
double addFive(double x) {
  return x + 5;
};
```

## Operators

### Numerical

```amiscript
5 + 4 - 2 == 7;
(3 * 4) / 6 == 2;
10 % 3 == 1;
```

### Numerical Assignment

```amiscript
int x = 2;
x++; // x is 3
x += 3; // x is 6
x *= 2; // x is 12
x /= 4; // x is 3
x %= 2; // x is 1
```

### Boolean

```amiscript
Boolean b1 = (5 < 10); // T
Boolean b2 = (3 >= 4); // F
Boolean b3 = (1 == 1); // T
(b1 && (b2 || b3)); // T
!b1; // F
```

### Regex

See [Java Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html) for `=~`

```amiscript
String myRegex = "id[0-9][0-9]";
"id0a" =~ myRegex; // F
"id01" =~ myRegex; // T
```

See [Simplified Text Matching](../reference/ami_script.md#simplified-text-matching) for `~~`

```amiscript
String mySimplifiedTextMatching = "^id*|^myid*";
"yourid01" ~~ mySimplifiedTextMatching; // F
"myid01" ~~ mySimplifiedTextMatching; // T
```