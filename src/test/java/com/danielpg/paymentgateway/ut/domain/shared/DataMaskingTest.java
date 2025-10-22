package com.danielpg.paymentgateway.ut.domain.shared;

import com.danielpg.paymentgateway.domain.shared.DataMasking;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataMaskingTest {

    @Test
    void maskNameMasksCorrectly() {
        assertThat(DataMasking.maskName("João da Silva"), is("Jo*********va"));
        assertThat(DataMasking.maskName("Guimarães"), is("Gu*****es"));
        assertThat(DataMasking.maskName("Pereira"), is("*******"));
        assertThat(DataMasking.maskName(null), is(""));
    }

    @Test
    void maskEmailMasksCorrectly() {
        assertThat(DataMasking.maskEmail("daniel.pereira@gmail.com"), is("d************a@gmail.com"));
        assertThat(DataMasking.maskEmail("pereira@empresa.com"), is("p*****a@empresa.com"));
        assertThat(DataMasking.maskEmail("alice@x.com"), is("*****@x.com"));
        assertThat(DataMasking.maskEmail(null), is(""));
    }

    @Test
    void maskCpfMasksCorrectly() {
        assertThat(DataMasking.maskCpf("1234567890"), is("12******90"));
        assertThat(DataMasking.maskCpf("123"), is("***"));
        assertThat(DataMasking.maskCpf(null), is(""));
        assertThat(DataMasking.maskCpf("123.456.789-1"), is("12******91"));
    }
}
