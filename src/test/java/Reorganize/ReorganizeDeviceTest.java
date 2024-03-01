package Reorganize;

import devices.Device;
import devices.FailingPolicy;
import devices.StandardDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ReorganizeDeviceTest {
    private Device device;
    FailingPolicy failingPolicy;

    @BeforeEach
    void createDeviceAndPolicy(){
        failingPolicy =mock(FailingPolicy.class);
        device = new StandardDevice(failingPolicy);
    }
    @Test
    @DisplayName("Device creation with failing policy")
    void testDeviceCreationWithFailingPolicy() {
        assertDoesNotThrow(() -> new StandardDevice(failingPolicy));
    }
    @Test
    @DisplayName("Device is initially off")
    void testInitiallyOff() {
        assertFalse(device.isOn());
    }

    @Test
    @DisplayName("Device can be switched on")
    void testCanBeSwitchedOn(){
        when(this.failingPolicy.attemptOn()).thenReturn(true);
        verify(this.failingPolicy, times(0)).attemptOn();
        device.on();
        verify(this.failingPolicy, times(1)).attemptOn();
        assertTrue(device.isOn());
    }

    @Test
    @DisplayName("Device won't switched on if failing")
    void testWontSwitchedOn(){
        when(this.failingPolicy.attemptOn()).thenReturn(false);
        assertThrows(IllegalStateException.class, ()-> device.on());
    }

    @Test
    @DisplayName("Device can be switched off")
    void testCanBeSwitchedOff(){
        when(this.failingPolicy.attemptOn()).thenReturn(true);
        verify(this.failingPolicy, times(0)).attemptOn();
        device.on();
        verify(this.failingPolicy, times(1)).attemptOn();
        assertTrue(device.isOn());
        device.off();
        assertFalse(device.isOn());
    }

    @Test
    @DisplayName("Device can be reset")
    void testReset(){
        when(this.failingPolicy.attemptOn()).thenReturn(true);
        device.on();
        device.reset();
        assertEquals(2,
                Mockito.mockingDetails(this.failingPolicy).getInvocations().size());
    }

    @Test
    @DisplayName("Device switch on and off until failing")
    void testSwitchesOnAndOff() {
        when(this.failingPolicy.attemptOn()).thenReturn(true, true, false);
        IntStream.range(0, 2).forEach(i -> {
            device.on();
            assertTrue(device.isOn());
            device.off();
            assertFalse(device.isOn());
        });
        assertThrows(IllegalStateException.class, () -> device.on());
    }



}
