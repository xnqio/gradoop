/*
 * Copyright © 2014 - 2019 Leipzig University (Database Research Group)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradoop.storage.accumulo.utils;

import org.gradoop.common.model.api.entities.Element;
import org.gradoop.storage.accumulo.impl.predicate.filter.impl.AccumuloLabelIn;
import org.gradoop.storage.accumulo.impl.predicate.filter.impl.AccumuloLabelReg;
import org.gradoop.storage.accumulo.impl.predicate.filter.impl.AccumuloPropEquals;
import org.gradoop.storage.accumulo.impl.predicate.filter.impl.AccumuloPropLargerThan;
import org.gradoop.storage.accumulo.impl.predicate.filter.impl.AccumuloPropReg;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * Accumulo filters collection utils
 */
public class AccumuloFilters {

  /**
   * Label in formula generator function
   *
   * @param value value ranges
   * @param <T> epgm element type
   * @return label in formula
   */
  @Nonnull
  public static <T extends Element> AccumuloLabelIn<T> labelIn(
    @Nonnull String... value
  ) {
    return new AccumuloLabelIn<>(value);
  }

  /**
   * Label regex formula generator function
   *
   * @param reg regex pattern
   * @param <T> epgm element type
   * @return label regex formula
   */
  @Nonnull
  public static <T extends Element> AccumuloLabelReg<T> labelReg(
    @Nonnull Pattern reg
  ) {
    return new AccumuloLabelReg<>(reg);
  }

  /**
   * Property equals formula generator function
   *
   * @param key property key
   * @param value property value
   * @param <T> epgm element type
   * @return label regex formula
   */
  @Nonnull
  public static <T extends Element> AccumuloPropEquals<T> propEquals(
    @Nonnull String key,
    @Nonnull Object value
  ) {
    return new AccumuloPropEquals<>(key, value);
  }

  /**
   * Property larger than formula generator function
   *
   * @param key property key
   * @param value property value
   * @param include should include value
   * @param <T> epgm element type
   * @return property larger than formula
   */
  @Nonnull
  public static <T extends Element> AccumuloPropLargerThan<T> propLargerThan(
    @Nonnull String key,
    Object value,
    boolean include
  ) {
    return new AccumuloPropLargerThan<>(key, value, include);
  }

  /**
   * Property regex formula generator function
   *
   * @param key property key
   * @param reg property regex pattern
   * @param <T> epgm element type
   * @return property regex formula
   */
  @Nonnull
  public static <T extends Element> AccumuloPropReg<T> propReg(
    @Nonnull String key,
    @Nonnull Pattern reg
  ) {
    return new AccumuloPropReg<>(key, reg);
  }

}
