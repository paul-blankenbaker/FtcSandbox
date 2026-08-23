package org.firstinspires.ftc.teamcode.learn;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

/**
 * Examples of declaring Java variables and literals using the 8 Java primitive types.
 */
public class JavaPrimitivesTest {

    // Integers

    // int types are signed 32-bit integers that range from -2,147,483,648 to 2,147,483,647
    // For integer based calculations, this is typically the preferred type
    int myInt;

    // long types are signed 64-bit integers that range from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
    // long types take more memory and some calculations like division are slower than ints
    long myLong;

    // byte types are signed 8-bit integers that range from -128 to 127
    // not typically used for calculations do to the range, more commonly used to store binary data
    byte myByte;

    // short types are signed 16-bit integers that range from -32,768 to 32,767
    // not typically used for calculations do to the range, more commonly used to store binary data
    short myShort;

    // Reals

    // double types are 64-bit floating point numbers that range from 4.9E-324 to 1.8E308.
    // They have 15 to 17 significant decimal digits of accuracy and are preferred for sensor data.
    double myDouble;

    // float types are 32-bit floating point numbers that range from 1.4E-45 to 3.4E38
    // (6 to 7 significant decimal digits of accuracy)
    float myFloat;

    // Logic

    // boolean types are true or false and often used with conditional statements (if, while, etc.)
    boolean myBoolean;

    // Text

    // char types are used to hold 16-bit Unicode characters.
    char myChar;

    @Test
    public void testIntPrimitives() {
        // 1. INITIAL VALUE
        // When declared as a class field (above), Java automatically initializes ints to 0.
        // PRO TIP: Local variables (inside a method) are NOT initialized and will cause a compiler error if used!
        assertEquals(0, myInt);

        // 2. SETTING LITERAL VALUES
        // You can use underscores to make large numbers readable (like a comma)
        myInt = 2_000_000_000;
        assertEquals(2000000000, myInt);

        // Hexadecimal (base 16) is often used for colors or bitmasks in robotics (starts with 0x)
        myInt = 0xFF;
        assertEquals(255, myInt);

        // 3. SIMPLE CALCULATIONS & COMPOUND ASSIGNMENTS
        myInt = 10;
        myInt = myInt + 5; // Standard assignment
        myInt += 5;        // Compound assignment (adds 5 to the existing value of myInt)
        myInt *= 2;        // Compound assignment (multiplies existing value by 2)
        myInt++;           // Increment: A shorthand to add exactly 1
        assertEquals(41, myInt);

        // Order of operations (PEMDAS) still applies
        myInt = (6 / 2) + (4 * 7) - 1;
        assertEquals(30, myInt);

        // Division & Modulo
        // Integer division TRUNCATES (removes the decimal). 13 / 5 is 2.6, so Java gives 2.
        assertEquals(2, 13 / 5);
        // Modulo (%) gives you the remainder of that division.
        assertEquals(3, 13 % 5);

        // 4. SYSTEM CONSTANTS
        // The Integer class provides these values so you don't have to memorize them.
        assertEquals(2147483647, Integer.MAX_VALUE);
        assertEquals(-2147483648, Integer.MIN_VALUE);

        // 5. ROLLOVER (OVERFLOW)
        // Think of the 32-bit memory space like a circular number line or an odometer. 
        // When you add 1 to the maximum positive value, the physical bits "wrap around" 
        // to the maximum negative value.
        myInt = Integer.MAX_VALUE;
        myInt = myInt + 1;
        assertEquals(Integer.MIN_VALUE, myInt);

        // Likewise, subtracting 1 from the minimum value wraps back to the maximum.
        myInt = Integer.MIN_VALUE;
        myInt = myInt - 1;
        assertEquals(Integer.MAX_VALUE, myInt);
    }

    @Test
    public void testLongPrimitives() {
        // Longs are 64-bit. Use them for high-resolution timers or massive encoder counts.
        // 1. LITERALS: You MUST add an 'L' at the end of the number!
        myLong = 3_000_000_000L;
        assertEquals(3000000000L, myLong);

        // 2. CONSTANTS
        // The range is astronomical compared to int
        assertEquals(9223372036854775807L, Long.MAX_VALUE);

        // 3. ROLLOVER
        myLong = Long.MAX_VALUE;
        assertEquals(Long.MIN_VALUE, myLong + 1);
    }

    @Test
    public void testByteAndShort() {
        // These are small integers used to save memory in large arrays or for raw hardware data.

        // Byte: 8-bit (-128 to 127)
        myByte = 127;
        myByte++;
        assertEquals(-128, myByte); // Rollover happens fast!

        // Short: 16-bit (-32,768 to 32,767)
        myShort = 32767;
        myShort++;
        assertEquals(-32768, (short) myShort);
    }

    @Test
    public void testDoubleAndFloat() {
        // 1. PRECISION
        // Doubles are the default for Math functions.
        // NOTE: In tests, we use a "delta" (0.0001) because floating point math isn't perfectly exact!
        myDouble = 0.1 + 0.2;
        Assert.assertNotEquals(0.3, myDouble); // Surprise! It's actually 0.30000000000000004
        assertEquals(0.3, myDouble, 0.0001);

        // 2. FLOAT LITERALS
        // Floats MUST have an 'f' suffix. They have less precision than doubles.
        myFloat = 3.14f;
        assertEquals(3.14f, myFloat, 0.001f);

        // 3. SPECIAL VALUES
        // Unlike ints, dividing a double by 0 doesn't crash; it creates "Infinity"
        assertEquals(Double.POSITIVE_INFINITY, 1.0 / 0.0, 0.0);
        Assert.assertTrue(Double.isNaN(0.0 / 0.0)); // NaN = "Not a Number"
    }

    @Test
    public void testBooleanPrimitives() {
        // Booleans are the "brains" of logic (if/while statements)
        myBoolean = true;

        // Logical Operators
        Assert.assertTrue(myBoolean && true);  // AND
        Assert.assertTrue(myBoolean || false); // OR
        Assert.assertFalse(!myBoolean);        // NOT (Invert)
    }

    @Test
    public void testCharPrimitives() {
        // Chars store a single 16-bit Unicode character.
        // Use single quotes '' for chars, double quotes "" for Strings!
        myChar = 'A';
        assertEquals(65, (int) myChar); // 'A' is actually the number 65 in memory (ASCII)

        // Unicode Examples
        char checkMark = '✅';
        char radioactive = '☢';
        char biohazard = '☣';

        assertEquals('✅', checkMark);

        // Math with chars? Sort of! You can increment a letter to get the next one.
        myChar = 'A';
        myChar++;
        assertEquals('B', myChar);

        // However, most operations won't work
        // myChar = checkMark + radioactive;

        // You can use \uDDDD where DDDD is the four-digit hex code for the unicode character, but
        // this typically isn't necessary in modern coding environments
        assertEquals('\u2622', radioactive);

        // Characters can be appended to Strings with the + operator, the results are
        // String types which not a primitive but a collection of characters
        String message = "Danger " + biohazard;
        assertEquals("Danger ☣", message);
    }

}
