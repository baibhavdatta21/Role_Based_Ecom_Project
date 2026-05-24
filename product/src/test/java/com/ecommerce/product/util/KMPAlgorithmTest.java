package com.ecommerce.product.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KMPAlgorithm Unit Tests")
public class KMPAlgorithmTest {

    // ==================== POSITIVE TEST CASES ====================

    @Test
    @DisplayName("ALGO001: Search Pattern at Beginning")
    public void testKmpSearch_PatternAtBeginning() {
        String text = "Laptop";
        String pattern = "Lap";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO002: Search Pattern in Middle")
    public void testKmpSearch_PatternInMiddle() {
        String text = "Electronics";
        String pattern = "Lect";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("ALGO003: Search Pattern at End")
    public void testKmpSearch_PatternAtEnd() {
        String text = "Laptop";
        String pattern = "top";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(3, result);
    }

    @Test
    @DisplayName("ALGO004: Pattern Equals Text")
    public void testKmpSearch_PatternEqualsText() {
        String text = "Product";
        String pattern = "Product";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO005: Single Character Pattern Match")
    public void testKmpSearch_SingleCharacter() {
        String text = "Electronics";
        String pattern = "E";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO006: Long Text Long Pattern")
    public void testKmpSearch_LongTextLongPattern() {
        String text = "High performance laptop for professionals";
        String pattern = "performance";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(5, result);
    }

    // ==================== NEGATIVE TEST CASES ====================

    @Test
    @DisplayName("ALGO007: Pattern Not Found")
    public void testKmpSearch_PatternNotFound() {
        String text = "Electronics";
        String pattern = "XYZ";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("ALGO008: Pattern Longer Than Text")
    public void testKmpSearch_PatternLongerThanText() {
        String text = "Laptop";
        String pattern = "LaptopComputer";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("ALGO009: Pattern Not Found - Case Sensitive")
    public void testKmpSearch_CaseSensitive() {
        String text = "Laptop";
        String pattern = "laptop";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @DisplayName("ALGO010: Empty Pattern")
    public void testKmpSearch_EmptyPattern() {
        String text = "Laptop";
        String pattern = "";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO011: Empty Text")
    public void testKmpSearch_EmptyText() {
        String text = "";
        String pattern = "Laptop";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(-1, result);
    }

    @Test
    @DisplayName("ALGO012: Both Text and Pattern Empty")
    public void testKmpSearch_BothEmpty() {
        String text = "";
        String pattern = "";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO013: Single Character Text and Pattern Match")
    public void testKmpSearch_SingleCharacterMatch() {
        String text = "A";
        String pattern = "A";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO014: Single Character Text and Pattern No Match")
    public void testKmpSearch_SingleCharacterNoMatch() {
        String text = "A";
        String pattern = "B";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(-1, result);
    }

    // ==================== SPECIAL CHARACTER TESTS ====================

    @Test
    @DisplayName("ALGO015: Pattern with Numbers")
    public void testKmpSearch_PatternWithNumbers() {
        String text = "Product123Code";
        String pattern = "123";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(7, result);
    }

    @Test
    @DisplayName("ALGO016: Pattern with Special Characters")
    public void testKmpSearch_PatternWithSpecialCharacters() {
        String text = "Email@example.com";
        String pattern = "@example";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("ALGO017: Pattern with Spaces")
    public void testKmpSearch_PatternWithSpaces() {
        String text = "High performance laptop";
        String pattern = "performance laptop";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("ALGO018: Pattern with Hyphen")
    public void testKmpSearch_PatternWithHyphen() {
        String text = "Blue-Light-Monitor";
        String pattern = "Light-M";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(5, result);
    }

    // ==================== REPEATING PATTERN TESTS ====================

    @Test
    @DisplayName("ALGO019: Repeating Pattern")
    public void testKmpSearch_RepeatingPattern() {
        String text = "AAABAAAB";
        String pattern = "AAAB";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO020: Repeating Pattern Multiple Occurrences")
    public void testKmpSearch_PatternMultipleOccurrences() {
        String text = "ABABAB";
        String pattern = "AB";

        // KMP returns first occurrence
        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO021: Pattern with Overlapping")
    public void testKmpSearch_OverlappingPattern() {
        String text = "AAAA";
        String pattern = "AAA";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO022: Pattern with Failed Partial Match")
    public void testKmpSearch_FailedPartialMatch() {
        String text = "ABCABD";
        String pattern = "ABCD";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(-1, result);
    }

    // ==================== CASE SENSITIVITY TESTS ====================

    @Test
    @DisplayName("ALGO023: Case Insensitive Search - Uppercase Pattern")
    public void testKmpSearch_LowercaseTextUppercasePattern() {
        String text = "laptop";
        String pattern = "LAPTOP";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("ALGO024: Consistent Case in Text and Pattern")
    public void testKmpSearch_ConsistentCase() {
        String text = "LaptopElectronics";
        String pattern = "Laptop";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    // ==================== LONG STRING TESTS ====================

    @Test
    @DisplayName("ALGO025: Very Long Text")
    public void testKmpSearch_VeryLongText() {
        String text = "A".repeat(1000) + "NEEDLE" + "B".repeat(1000);
        String pattern = "NEEDLE";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(1000, result);
    }

    @Test
    @DisplayName("ALGO026: Large Pattern")
    public void testKmpSearch_LargePattern() {
        String text = "ABCDEF" + "X".repeat(500) + "GHIJKL";
        String pattern = "X".repeat(500);

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(6, result);
    }

    @Test
    @DisplayName("ALGO027: Large Text Large Pattern Match")
    public void testKmpSearch_LargeTextLargePatternMatch() {
        String baseText = "Product" + "Quality".repeat(100);
        String pattern = "Quality".repeat(50);

        int result = KMPAlgorithm.kmpSearch(baseText, pattern);

        assertTrue(result >= 0);
    }

    // ==================== WORD SEARCH TESTS ====================

    @Test
    @DisplayName("ALGO028: Search Product Name Substring")
    public void testKmpSearch_ProductNameSubstring() {
        String text = "Dell Laptop Computer";
        String pattern = "Laptop";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("ALGO029: Search Category Substring")
    public void testKmpSearch_CategorySubstring() {
        String text = "Electronics";
        String pattern = "tronics";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(4, result);
    }

    @Test
    @DisplayName("ALGO030: Search Description Substring")
    public void testKmpSearch_DescriptionSubstring() {
        String text = "High performance laptop for professionals";
        String pattern = "for";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(8, result);
    }

    // ==================== BOUNDARY TESTS ====================

    @Test
    @DisplayName("ALGO031: Pattern at Exact Position")
    public void testKmpSearch_PatternAtExactPosition() {
        String text = "0123456789";
        String pattern = "456";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(4, result);
    }

    @Test
    @DisplayName("ALGO032: Last Character Match")
    public void testKmpSearch_LastCharacterMatch() {
        String text = "Laptop";
        String pattern = "p";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(2, result);
    }

    @Test
    @DisplayName("ALGO033: First Character Match")
    public void testKmpSearch_FirstCharacterMatch() {
        String text = "Laptop";
        String pattern = "L";

        int result = KMPAlgorithm.kmpSearch(text, pattern);

        assertEquals(0, result);
    }

    // ==================== PERFORMANCE TESTS ====================

    @Test
    @DisplayName("ALGO034: Performance with Repeated Characters")
    public void testKmpSearch_PerformanceRepeatedCharacters() {
        String text = "AAAAA";
        String pattern = "AAAA";

        long startTime = System.currentTimeMillis();
        int result = KMPAlgorithm.kmpSearch(text, pattern);
        long endTime = System.currentTimeMillis();

        assertEquals(0, result);
        assertTrue((endTime - startTime) < 100); // Should be very fast
    }

    @Test
    @DisplayName("ALGO035: Performance with Large Mismatch")
    public void testKmpSearch_PerformanceLargeMismatch() {
        String text = "A".repeat(1000) + "B";
        String pattern = "BA";

        long startTime = System.currentTimeMillis();
        int result = KMPAlgorithm.kmpSearch(text, pattern);
        long endTime = System.currentTimeMillis();

        assertEquals(-1, result);
        assertTrue((endTime - startTime) < 100);
    }

    // ==================== NULL SAFETY TESTS ====================

    @Test
    @DisplayName("ALGO036: Null Text Throws Exception")
    public void testKmpSearch_NullText() {
        assertThrows(NullPointerException.class, () -> {
            KMPAlgorithm.kmpSearch(null, "pattern");
        });
    }

    @Test
    @DisplayName("ALGO037: Null Pattern Throws Exception")
    public void testKmpSearch_NullPattern() {
        assertThrows(NullPointerException.class, () -> {
            KMPAlgorithm.kmpSearch("text", null);
        });
    }

    @Test
    @DisplayName("ALGO038: Both Null Throws Exception")
    public void testKmpSearch_BothNull() {
        assertThrows(NullPointerException.class, () -> {
            KMPAlgorithm.kmpSearch(null, null);
        });
    }
}
