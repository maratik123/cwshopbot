//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.cwshopbot.utils;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.beans.PropertyUtil;
import org.hamcrest.beans.SamePropertyValuesAs;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class SamePropertyValuesAsExcept<T> extends TypeSafeDiagnosingMatcher<T> {
    private final T expectedBean;
    private final Set<String> propertyNames;
    private final Set<String> exceptPropertyNames;
    private final List<SamePropertyValuesAs.PropertyMatcher> propertyMatchers;


    public SamePropertyValuesAsExcept(T expectedBean, String... exceptPropertyNames) {
        this.exceptPropertyNames = Stream.of(exceptPropertyNames)
            .collect(Collectors.toSet());
        List<PropertyDescriptor> descriptors = propertyDescriptorsFor(expectedBean, false);
        this.expectedBean = expectedBean;
        this.propertyNames = propertyNamesFrom(descriptors);
        this.propertyMatchers = propertyMatchersFor(expectedBean, descriptors);
    }

    private List<PropertyDescriptor> propertyDescriptorsFor(Object fromObj, boolean excludeExpectedPropertyNames) {
        Stream<PropertyDescriptor> stream = Stream.of(PropertyUtil.propertyDescriptorsFor(fromObj, Object.class))
            .filter(propertyDescriptor -> !exceptPropertyNames.contains(propertyDescriptor.getName()));
        if (excludeExpectedPropertyNames) {
            stream = stream.filter(propertyDescriptor -> !propertyNames.contains(propertyDescriptor.getName()));
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public boolean matchesSafely(T bean, Description mismatch) {
        return isCompatibleType(bean, mismatch)
            && hasNoExtraProperties(bean, mismatch)
            && hasMatchingValues(bean, mismatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("same property values as " + expectedBean.getClass().getSimpleName())
            .appendList(" [", ", ", "]", propertyMatchers);
    }


    private boolean isCompatibleType(T item, Description mismatchDescription) {
        if (!expectedBean.getClass().isAssignableFrom(item.getClass())) {
            mismatchDescription.appendText("is incompatible type: " + item.getClass().getSimpleName());
            return false;
        }
        return true;
    }

    private boolean hasNoExtraProperties(T item, Description mismatchDescription) {
        Set<String> actualPropertyNames = propertyNamesFrom(propertyDescriptorsFor(item, true));
        if (!actualPropertyNames.isEmpty()) {
            mismatchDescription.appendText("has extra properties called " + actualPropertyNames);
            return false;
        }
        return true;
    }

    private boolean hasMatchingValues(T item, Description mismatchDescription) {
        return propertyMatchers.stream()
            .filter(propertyMatcher -> !propertyMatcher.matches(item))
            .findAny()
            .map(propertyMatcher -> {
                propertyMatcher.describeMismatch(item, mismatchDescription);
                return false;
            }).orElse(true);
    }

    private static <T> List<SamePropertyValuesAs.PropertyMatcher> propertyMatchersFor(T bean, List<PropertyDescriptor> descriptors) {
        return descriptors.stream()
            .map(propertyDescriptor -> new SamePropertyValuesAs.PropertyMatcher(propertyDescriptor, bean))
            .collect(Collectors.toList());
    }

    private static Set<String> propertyNamesFrom(List<PropertyDescriptor> descriptors) {
        return descriptors.stream()
            .map(PropertyDescriptor::getName)
            .collect(Collectors.toSet());
    }

    /**
     * Creates a matcher that matches when the examined object has values for all of
     * its JavaBean properties that are equal to the corresponding values of the
     * specified bean.
     * <p/>
     * For example:
     * <pre>assertThat(myBean, samePropertyValuesAsExcept(myExpectedBean, "mutableProperty"))</pre>
     *
     * @param expectedBean the bean against which examined beans are compared
     */
    @Factory
    public static <T> Matcher<? super T> samePropertyValuesAsExcept(T expectedBean, String... exceptProperties) {
        return new SamePropertyValuesAsExcept<>(expectedBean, exceptProperties);
    }
}
