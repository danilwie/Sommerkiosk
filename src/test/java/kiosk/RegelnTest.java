package kiosk;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegelnTest {
    @Test void geldExakt() {
        assertEquals(150,Regeln.cent("1,50")); assertEquals(150,Regeln.cent("1.50"));
        assertEquals(1,Regeln.cent("0,01")); assertEquals(120,Regeln.cent("1,2")); assertEquals(100,Regeln.cent("1"));
        assertEquals("0,01 €",Regeln.euro(1)); assertEquals(Integer.MAX_VALUE,Regeln.cent("21474836,47"));
    }
    @Test void falschePreise() {
        for (String s : new String[]{"0","-1","","abc","1,501","1.000,00","1e1","21474836,48","+1",".50"})
            assertThrows(Ablehnung.class,()->Regeln.cent(s),s);
    }
    @Test void namenGrenzen() {
        assertEquals("Mia",Regeln.name(" Mia ")); assertEquals(100,Regeln.name("a".repeat(100)).length());
        for (String n : new String[]{"  ","a".repeat(101),"A\nB"}) assertThrows(Ablehnung.class,()->Regeln.name(n));
    }
    @Test void restschutzIsoliert() {
        // Manipulationsfall: Unter korrekter Zehntageshistorie ist das Tageslimit mindestens ebenso eng.
        var tag = LocalDate.of(2026,7,1);
        assertTrue(assertThrows(Ablehnung.class,()->Regeln.pruefeKauf(tag,tag,true,51,50,0)).getMessage().contains("Restbudget"));
    }
    @Test void keinUeberlaufBeiHohemPreis() {
        var tag = LocalDate.of(2026,7,1);
        assertThrows(Ablehnung.class,()->Regeln.pruefeKauf(tag,tag,true,Integer.MAX_VALUE,1500,149));
    }
}
