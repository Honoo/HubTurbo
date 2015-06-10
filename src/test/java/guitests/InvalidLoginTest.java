package guitests;

import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.controls.Commons.hasText;

import org.junit.Test;
import org.loadui.testfx.utils.FXTestUtils;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class InvalidLoginTest extends UITest {

    @Override
    public void launchApp() {
        FXTestUtils.launchApp(TestUI.class, "--test=true");
    }

    @Test
    public void invalidLoginTest() throws InterruptedException {
        TextField repoOwnerField = find("#repoOwnerField");
        doubleClick(repoOwnerField);
        doubleClick(repoOwnerField);
        type("abc").push(KeyCode.TAB);
        type("abc").push(KeyCode.TAB);
        type("abc").push(KeyCode.TAB);
        type("abc");
        click("Sign in");
        assertNodeExists(hasText("Failed to sign in. Please try again."));
    }
}
