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
package org.gradoop.flink.model.impl.operators.intersection;

import org.gradoop.flink.model.impl.epgm.GraphCollection;
import org.gradoop.flink.model.impl.operators.base.BinaryCollectionOperatorsTestBase;
import org.gradoop.flink.util.FlinkAsciiGraphLoader;
import org.junit.Test;

public class ReduceIntersectionTest extends BinaryCollectionOperatorsTestBase {

  @Test
  public void testOverlappingCollections() throws Exception {
    FlinkAsciiGraphLoader loader = getSocialNetworkLoader();

    GraphCollection col02 = loader.getGraphCollectionByVariables("g0", "g2");

    GraphCollection col12 = loader.getGraphCollectionByVariables("g1", "g2");

    GraphCollection expectation = loader.getGraphCollectionByVariables("g2");

    GraphCollection result = col02.intersect(col12);
    checkAssertions(expectation, result, "");

    result = col02.intersectWithSmallResult(col12);
    checkAssertions(expectation, result, "small");
  }

  @Test
  public void testNonOverlappingCollections() throws Exception {
    FlinkAsciiGraphLoader loader = getSocialNetworkLoader();

    GraphCollection col01 = loader.getGraphCollectionByVariables("g0", "g1");

    GraphCollection col23 = loader.getGraphCollectionByVariables("g2", "g3");

    GraphCollection expectation = getConfig().getGraphCollectionFactory().createEmptyCollection();

    GraphCollection result = col01.intersect(col23);

    checkAssertions(expectation, result, "non");

    result = col01.intersectWithSmallResult(col23);
    checkAssertions(expectation, result, "small non");
  }

  @Test
  public void testTotalOverlappingCollections() throws Exception {
    FlinkAsciiGraphLoader loader = getSocialNetworkLoader();

    GraphCollection expectation = loader
      .getGraphCollectionByVariables("g0", "g1");

    GraphCollection result = expectation.intersect(expectation);
    checkAssertions(expectation, result, "total");

    result = expectation.intersectWithSmallResult(expectation);
    checkAssertions(expectation, result, "small total");
  }
}
