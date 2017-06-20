package com.github.gino0631.icns.maven;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class IcnsMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
    };

    @Test
    public void test() throws Exception {
        Mojo mojo = rule.lookupConfiguredMojo(new File("target/test-classes/test-project"), "icns");
        assertNotNull(mojo);
        mojo.execute();
    }
}
