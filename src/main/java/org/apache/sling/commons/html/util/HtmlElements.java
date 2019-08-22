/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.commons.html.util;

/**
 * Utility class to collect elements at the end of a stream and convert back to 
 * HTML text
 * 
 */
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.sling.commons.html.AttrValue;
import org.apache.sling.commons.html.HtmlElement;
import org.apache.sling.commons.html.impl.tag.StartTag;

public class HtmlElements implements Collector<HtmlElement, HtmlElements, String> {

    private StringBuilder sb = new StringBuilder();
    
    public HtmlElements() {
        //to allow for supplier method
    }
    
    public HtmlElements append(HtmlElement element) {
        sb.append(TO_HTML.apply(element));
        return this;
    }
    
    public HtmlElements append(HtmlElements collector) {
        sb.append(collector.toString());
        return this;
    }
    
    
    @Override
    public Supplier<HtmlElements> supplier() {
        return HtmlElements::new;
    }

    @Override
    public BiConsumer<HtmlElements, HtmlElement> accumulator() {
        return HtmlElements::append;
    }

    @Override
    public BinaryOperator<HtmlElements> combiner() {
        return (left,right) -> left.append(right);
    }

    @Override
    public Function<HtmlElements, String> finisher() {
        return HtmlElements::toString;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }
    
    public String toString() {
        return sb.toString();
    }
    
    public static final  Function<HtmlElement, String> TO_HTML = element ->{
        StringBuilder buffer = new StringBuilder();
        switch (element.getType()) {
        case COMMENT:
            buffer.append("<!--");
            buffer.append(element.getValue());
            buffer.append("-->");
            break;
        case DOCTYPE:
            buffer.append("<!");
            buffer.append(element.getValue());
            buffer.append(">");
            break;
        case END_TAG:
            buffer.append("</");
            buffer.append(element.getValue());
            buffer.append('>');
            break;
        case EOF:
            break;
        case START_TAG:
            buffer.append('<');
            buffer.append(element.getValue());
            StartTag tag = (StartTag) element;
            if (tag.hasAttributes()) {
                buffer.append(' ');
                buffer.append(tag.getAttributes().entrySet().stream().map(entry -> {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(entry.getKey());
                    AttrValue value = entry.getValue();
                    if (!value.isEmpty()) {
                        sb2.append("=");
                        sb2.append(value.quoteIfNeeded());
                    } 
                    return sb2.toString();
                }).collect(Collectors.joining(" ")));
            }
            buffer.append('>');
            break;
        case TEXT:
            buffer.append(element.toString());
        }
        return buffer.toString();
    };
    
    public static HtmlElements  elementsToHtml() {
        return new HtmlElements();
    }

}
