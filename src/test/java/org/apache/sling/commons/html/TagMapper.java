/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.commons.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.sling.commons.html.HtmlElement;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Utility class that allows you to define a flatMap process in the form of a
 * BiConsumer<Element,TagMapping> lambda.
 * 
 * This allows you to use the next() method to collect the elements that will be
 * passed on to the stream method. This can modify the eventual output and assists in use cases where
 * there is a need to add or remove elements
 *
 */
@ProviderType
public class TagMapper {

    private List<HtmlElement> list = new ArrayList<>();

    private TagMapper() {
    }

    /**
     * Collects all the elements that are either being passed through or created in
     * the accept method of the consumer so that they may be passed on to the next
     * process.
     */
    public void next(HtmlElement... elements) {
        Collections.addAll(list, elements);
    }

    Function<HtmlElement, Stream<HtmlElement>> createFlatMap(BiConsumer<HtmlElement, TagMapper> consumer, TagMapper mapper) {
        return element -> {
            list.clear();
            consumer.accept(element, mapper);
            return list.stream();
        };
    }

    public static Function<HtmlElement, Stream<HtmlElement>> map(BiConsumer<HtmlElement, TagMapper> consumer) {
        TagMapper mapper = new TagMapper();
        return mapper.createFlatMap(consumer, mapper);
    }

}
