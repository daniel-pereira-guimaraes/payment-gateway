package com.danielpg.paymentgateway.ut.domain.shared;

import com.danielpg.paymentgateway.domain.shared.DataMasking;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataMaskingTest {

    @Test
    void maskNameMasksCorrectly() {
        assertThat(DataMasking.maskName("Daniel"), is("D****l"));
        assertThat(DataMasking.maskName("Alice"), is("A***e"));
        assertThat(DataMasking.maskName("Ana"), is("***"));
        assertThat(DataMasking.maskName(null), is(""));
    }

    @Test
    void maskEmailMasksCorrectly() {
        assertThat(DataMasking.maskEmail("daniel.pereira@gmail.com"), is("d************a@gmail.com"));
        assertThat(DataMasking.maskEmail("alice@empresa.com"), is("a***e@empresa.com"));
        assertThat(DataMasking.maskEmail("ana@x.com"), is("***@x.com"));
        assertThat(DataMasking.maskEmail(null), is(""));
    }

    @Test
    void maskCpfMasksCorrectly() {
        assertThat(DataMasking.maskCpf("1234567890"), is("1********0"));
        assertThat(DataMasking.maskCpf("123"), is("***"));
        assertThat(DataMasking.maskCpf(null), is(""));
        assertThat(DataMasking.maskCpf("123.456.789-1"), is("1********1"));
    }
}
