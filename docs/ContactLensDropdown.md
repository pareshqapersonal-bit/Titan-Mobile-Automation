# Contact Lens Power Dropdown – How the Selection Works

File: `src/test/java/com/titan/eyestage/v2/pom/ContactLensCartNew.java`

This document explains how the test selects a power value (for example `-2.25`) from
the SPH dropdown on the Contact Lens product page (PDP) of the new Flutter app. It covers
what was wrong before, what the screen looks like to Appium, and what each function and
call does.

---

## 1. The problem we had

The old `selectIfPresent` did this:

```java
dropdown.click();
driver.findElement(AppiumBy.androidUIAutomator(
    "new UiScrollable(new UiSelector().scrollable(true).instance(0)).setMaxSearchSwipes(15)"
  + ".scrollIntoView(new UiSelector().description(\"-2.25\"))"));
```

It failed for three reasons:

| # | Problem | Effect |
|---|---------|--------|
| 1 | `scrollable(true).instance(0)` picks the **first** scrollable on screen. That is the whole product page, not the dropdown list. | The page behind the popup scrolled; the list never moved, so `-2.25` never came into view → `NoSuchElementException`. |
| 2 | After selecting, the code read `leftSPH.getText()`, which uses the old native id `com.titan.eyecare:id/edt_sph_left`. | The Flutter app has no such id, so it crashed even after a correct selection. |
| 3 | The first tap on the dropdown is sometimes ignored while the page is still settling. | The list never opened, so nothing could be found. |

---

## 2. What the screen looks like to Appium

We took a UI dump (`adb shell uiautomator dump`) on the real device while on the PDP.

### Before opening the dropdown

```
ScrollView  scrollable=true  [0,253][1080,2062]     <- whole page
View        scrollable=true  [0,253][1080,383]      <- image carousel
View        content-desc="SPH"
Button      text="Select"   [326,1326][664,1436]    <- Left (OS) SPH
Button      text="Select"   [686,1326][1025,1436]   <- Right (OD) SPH
View        content-desc="CYL"
Button      text="Select" ... (x2)
View        content-desc="AXIS"
Button      text="Select" ... (x2)
View        content-desc="No of boxes"
Button      text="Select" ... (x2)
```

So there are **2 scrollables** on the page before the dropdown opens.

### After tapping Left SPH

A **third** scrollable appears – this is the dropdown list:

```
ScrollView  scrollable=true  [326,1447][664,1975]   <- dropdown list (528 px tall)
  View  content-desc="Select"
  View  content-desc="6"
  View  content-desc="5.75"
  View  content-desc="5.5"
  View  content-desc="5.25"
```

Important facts we learned from the device:

- Each option's value is in **`content-desc`** (not `text`).
- Values go **from high to low**: `6, 5.75 … 0.25, 0, -0.25 … -2, -2.25, -2.5 …`
- Negative values use a normal hyphen: `-2.25`. Whole numbers have no decimals: `6`, `0`, `-2`.
- The dropdown list is the **last** scrollable element in the hierarchy (it is drawn on top).
- Rows that are scrolled **outside** the list's visible area are still present in the
  hierarchy. If you tap one of those, the tap lands on the page behind, not on the option.
- About 9 swipes inside the list are needed to bring `-2.25` into view.
- After selecting, the button's text changes from `Select` to `-2.25`.

---

## 3. Locators

```java
// Left Eye
@FindBy(xpath = "//android.view.View[@content-desc='SPH']/following::android.widget.Button[1]")
WebElement leftSPHSelect;

// Right Eye
@FindBy(xpath = "//android.view.View[@content-desc='SPH']/following::android.widget.Button[2]")
WebElement rightSPHSelect;
```

How to read the XPath:

- `//android.view.View[@content-desc='SPH']` – find the "SPH" label in the Power table.
- `/following::android.widget.Button` – every Button that appears after it in the screen tree.
- `[1]` – the first one is the **Left (OS)** SPH button; `[2]` is the **Right (OD)** SPH button.

**Why we removed `[@text='Select']`:** once you choose a value, the button text becomes
`-2.25`. With the old locator `Button[@text='Select'][2]`, the "2nd button whose text is
Select" would then be a different button (Left CYL instead of Right SPH). Matching by position
keeps pointing to the correct button no matter what value is shown.

---

## 4. Flow of `addToCart(CartProduct product)`

```
addToCart()
  ├─ smallSwipeUp()                         scroll page a little so Power table is visible
  ├─ selectIfPresent(leftSPHSelect, "-2.25")
  │     ├─ open dropdown (with 1 retry)
  │     ├─ find the dropdown list's position/size
  │     └─ loop: is "-2.25" visible inside the list?
  │             yes → tap it, return
  │             no  → swipeInDropdownList(list, down) and check again
  │                   (if end of list reached → try scrolling up instead)
  ├─ smallSwipeUp()
  ├─ selectIfPresent(rightQty, "1")         quantity
  ├─ click(buyNowCTA)
  └─ click(skipOption)
```

---

## 5. `selectIfPresent(WebElement dropdown, String value)` – step by step

### Step 0 – Skip when no value is given

```java
if (value == null || value.trim().isEmpty()) {
    return;
}
```

If the test data has no value for this field, do nothing. This lets the same method be
used for optional fields.

### Step 1 – Open the dropdown and wait for the list

```java
By scrollableBy = By.xpath("//*[@scrollable='true']");
int before = driver.findElements(scrollableBy).size();
```

- `By.xpath("//*[@scrollable='true']")` – a locator for **every** element that can scroll.
- `driver.findElements(...)` – returns a list (empty list if none; it never throws).
- `before` – how many scrollables exist **before** opening the dropdown (2 on this page).

```java
Rectangle list = null;
for (int attempt = 0; attempt < 2 && list == null; attempt++) {
    click(dropdown);
    long end = System.currentTimeMillis() + 3000;
    while (System.currentTimeMillis() < end) {
        List<WebElement> scrollables = driver.findElements(scrollableBy);
        if (scrollables.size() > before) {
            list = scrollables.get(scrollables.size() - 1).getRect();
            break;
        }
    }
}
```

- The outer `for` tries **at most 2 times** to open the dropdown, and stops as soon as
  `list` is found.
- `click(dropdown)` – this is `CommonUtils.click()`. It waits for the element to be
  clickable (up to 30 s) and handles the Encircle popup if it shows up.
- The inner `while` keeps checking for **3 seconds** (`System.currentTimeMillis() + 3000`).
- `scrollables.size() > before` – a new scrollable appeared, which means the list opened.
- `scrollables.get(scrollables.size() - 1)` – take the **last** scrollable. The popup is
  drawn on top, so it comes last in the tree.
- `.getRect()` – returns a `Rectangle` with `x`, `y`, `width`, `height` of the list on
  screen. For Left SPH that is x=326, y=1447, width=338, height=528.
  We save this so we know exactly where the list is.

```java
if (list == null) {
    throw new RuntimeException("Dropdown list did not open for value '" + value + "'");
}
```

If after 2 taps the list still did not open, fail with a clear message instead of a vague
`NoSuchElementException` later on.

### Step 2 – Build the option locator

```java
By option = By.xpath("//*[@content-desc='" + value + "']");
```

For `value = "-2.25"` this becomes `//*[@content-desc='-2.25']` – any element whose
content-desc is exactly `-2.25`. Exact match is important: `contains` would wrongly
match things like `-12.25`.

### Step 3 – Search loop: scroll down first, then up

```java
boolean[] directions = {true, false};
for (boolean scrollDown : directions) {
```

- First pass: `scrollDown = true` – the list goes high → low, so negative values are below.
- Second pass: `scrollDown = false` – only used if the value was not found going down
  (for example, if the list did not open at the top).

```java
    String lastPage = "";
    for (int i = 0; i < 25; i++) {
```

At most 25 swipes per direction (we need about 9 for `-2.25`), so the test can never
loop forever.

#### 3a – Look for a visible match

```java
        for (WebElement match : driver.findElements(option)) {
            Rectangle r = match.getRect();
            int centerY = r.y + r.height / 2;
            if (centerY > list.y && centerY < list.y + list.height) {
                match.click();
                System.out.println("Clicked option: " + value);
                test().info("Selected: " + value);
                return;
            }
        }
```

- `driver.findElements(option)` – all elements with content-desc `-2.25` (could be 0 or 1).
- `match.getRect()` – the option's position on screen.
- `centerY` – the vertical middle of the option.
- `centerY > list.y && centerY < list.y + list.height` – is the middle of the option
  **inside** the visible list area? This is the key check. Without it we would tap rows
  that exist in the tree but are hidden, and the tap would land on the page behind.
- `match.click()` – taps the option.
- `test().info(...)` – writes a line into the Extent report.
- `return` – done, leave the method.

#### 3b – Detect end of list

```java
        String page = driver.getPageSource();
        if (page.equals(lastPage)) {
            break;
        }
        lastPage = page;
```

- `driver.getPageSource()` – the full XML of the current screen.
- If the screen is **exactly the same** as after the previous swipe, the list did not move,
  which means we hit the end. `break` stops this direction and moves on to the next one.

#### 3c – Swipe and try again

```java
        swipeInDropdownList(list, scrollDown);
    }
}
```

Swipe inside the list, then the loop checks again from 3a.

### Step 4 – Not found at all

```java
throw new RuntimeException("Option '" + value + "' not found in dropdown");
```

Searched both directions and the value does not exist (for example a typo in test data,
or a power this product does not offer). The message tells you exactly which value failed.

---

## 6. `swipeInDropdownList(Rectangle list, boolean scrollDown)`

Swipes **inside the dropdown list only**, so the page behind does not scroll.

```java
int x = list.x + list.width / 2;
int from = list.y + (int) (list.height * (scrollDown ? 0.8 : 0.2));
int to   = list.y + (int) (list.height * (scrollDown ? 0.2 : 0.8));
```

- `x` – horizontal middle of the list (326 + 338/2 = **495**).
- To scroll **down** (see lower values) the finger moves **up**: from 80% of the list
  height to 20%. For Left SPH: from y=1869 to y=1552.
- To scroll **up**, the opposite: from 20% to 80%.
- `scrollDown ? a : b` is the ternary operator: use `a` if `scrollDown` is true, else `b`.

```java
PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
Sequence swipe = new Sequence(finger, 1);
swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, from));
swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
swipe.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), x, to));
swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
driver.perform(List.of(swipe));
```

This is the W3C Actions API (the modern replacement for the old `TouchAction`):

| Call | Meaning |
|------|---------|
| `new PointerInput(Kind.TOUCH, "finger")` | Create a virtual finger. |
| `new Sequence(finger, 1)` | A list of actions this finger will perform. |
| `createPointerMove(Duration.ZERO, viewport(), x, from)` | Instantly place the finger at the start point. `viewport()` means coordinates are screen pixels. |
| `createPointerDown(LEFT)` | Touch the screen. (`LEFT` is just the "primary" pointer; for touch it means a finger press.) |
| `createPointerMove(400 ms, viewport(), x, to)` | Drag to the end point over 400 ms. A slower drag scrolls a controlled amount instead of flinging. |
| `createPointerUp(LEFT)` | Lift the finger. |
| `driver.perform(List.of(swipe))` | Send the whole gesture to the device. |

---

## 7. `smallSwipeUp()`

Same gesture idea as above, but on the **whole screen**: drags from 65% to 50% of the
screen height in 250 ms. It is used in `addToCart` to nudge the page up a little so the
Power table and quantity dropdowns are on screen before we tap them.

---

## 8. Reusing it for other dropdowns

The method works for any dropdown on this page that behaves the same way (a `Select`
button that opens a scrollable list whose options have the value in `content-desc`):

```java
selectIfPresent(rightSPHSelect, "-1.5");
```

For CYL / AXIS / No of boxes you can add locators the same positional way, for example:

```java
@FindBy(xpath = "//android.view.View[@content-desc='CYL']/following::android.widget.Button[1]")
WebElement leftCYLSelect;

@FindBy(xpath = "//android.view.View[@content-desc='No of boxes']/following::android.widget.Button[1]")
WebElement leftQtySelect;
```

Note: on the PDP we inspected, the CYL and AXIS buttons were greyed out (disabled) before
any SPH value was chosen. Check that they are enabled before trying to select from them.

---

## 9. Things still using old native ids

These fields in the class still use `com.titan.eyecare:id/...` ids from the old native app:
`rightCYL`, `rightAXIS`, `rightQty`, `leftSPH`, `leftCYL`, `leftAXIS`, `leftQty`,
`buyNowCTA`, `skipOption`. The Flutter screen dump showed no resource ids, so if any of
these steps start failing, replace them with content-desc / positional XPath locators like
the SPH ones above.

---

## 10. Quick debugging tips

Take a UI dump of the real device (Git Bash):

```bash
MSYS_NO_PATHCONV=1 adb shell uiautomator dump /sdcard/ui.xml
MSYS_NO_PATHCONV=1 adb pull /sdcard/ui.xml ui.xml
```

`MSYS_NO_PATHCONV=1` stops Git Bash on Windows from turning `/sdcard/...` into
`C:/Program Files/Git/sdcard/...`.

Swipe manually inside the left SPH list to test scrolling:

```bash
adb shell input swipe 495 1920 495 1500 400
```

Appium Inspector also works: open the dropdown, then look at the `content-desc` of the
rows and the `bounds` of the `ScrollView` that appears.
