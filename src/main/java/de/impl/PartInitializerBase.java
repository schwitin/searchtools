package de.impl;

import de.api.Part;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class PartInitializerBase {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public PartInitializerBase(final WebDriver driver, final WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public abstract void initPart(final Part part);

}
