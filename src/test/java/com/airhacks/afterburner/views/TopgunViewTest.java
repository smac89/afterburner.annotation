package com.airhacks.afterburner.views;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import com.airhacks.afterburner.injection.Injector;
import com.airhacks.afterburner.topgun.TopgunPresenter;
import com.airhacks.afterburner.topgun.TopgunView;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class TopgunViewTest {

    private TopgunView view;
    private TopgunPresenter presenter;

    @Before
    public void initialize() {
        this.view = new TopgunView();
        this.presenter = (TopgunPresenter) view.getPresenter();
    }

    @Test
    public void loadViewWithConfiguration() {
        Assert.assertNotNull(presenter);
        String host = presenter.getHost();
        String fireRate = presenter.getFireRate();
        Assert.assertNotNull(host);
        Assert.assertNotNull(fireRate);
        final String expectedHost = "tower"; // from configuration.properties
        final String expectedFireRate = "250"; // from configuration.properties using named injection
        assertThat(host, is(expectedHost));
        assertThat(fireRate, is(expectedFireRate));
    }

    @Test
    public void loadResourceBundle() {
        ResourceBundle bundle = this.view.getResourceBundle();
        assertNotNull(bundle);
        String value = bundle.getString("top");
        //value is fetched from the topgun.properties file
        assertThat(value, is("gun"));
    }

    @Test
    public void systemPropertyOverridesConfigurationProperties() {
        final String expected = "ivory tower";
        System.setProperty("host", expected);
        TopgunView newView = new TopgunView();
        TopgunPresenter newPresenter = (TopgunPresenter) newView.getPresenter();
        String actual = newPresenter.getHost();
        Assert.assertNotNull(actual);
        assertThat(actual, is(expected));
    }

    @Test
    public void getView() {
        Parent parent = this.view.getView();
        assertNotNull(parent);
    }

    @Test
    public void getPresenter() {
        Object object = this.view.getPresenter();
        assertNotNull(object);
    }

    @Test
    public void accessConventionalResourceBundle() {
        TopgunPresenter topPresenter = (TopgunPresenter) this.view.getPresenter();
        ResourceBundle bundle = topPresenter.getResourceBundle();
        assertNotNull(bundle);
    }

    @Test
    public void getPresengetViewWithoutRootContainerter() {
        Object object = this.view.getViewWithoutRootContainer();
        assertNotNull(object);
    }

    @Test
    public void singlePresenterPerView() {
        TopgunPresenter first = (TopgunPresenter) this.view.getPresenter();
        TopgunPresenter second = (TopgunPresenter) this.view.getPresenter();
        assertSame(first, second);
    }

    @Test
    public void perViewInjection() {
        Date actual = new Date();
        TopgunView v = new TopgunView(d -> actual);
        TopgunPresenter p = (TopgunPresenter) v.getPresenter();
        Date injected = p.getDate();
        assertThat(injected, is(actual));
    }

    @After
    public void cleanUp() {
        Injector.forgetAll();
        System.clearProperty("host");
    }

}
