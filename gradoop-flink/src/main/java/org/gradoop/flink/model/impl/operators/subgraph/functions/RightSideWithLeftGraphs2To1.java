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
package org.gradoop.flink.model.impl.operators.subgraph.functions;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.FunctionAnnotation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.api.entities.GraphElement;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.id.GradoopIdSet;

/**
 * {@code left, right => right} (retain graphIds contained in left)
 *
 * @param <R> right type
 */
@FunctionAnnotation.NonForwardedFieldsSecond("graphIds")
@FunctionAnnotation.ReadFieldsFirst("f1")
public class RightSideWithLeftGraphs2To1<R extends GraphElement>
  implements JoinFunction<Tuple2<GradoopId, GradoopIdSet>, R, R> {
  @Override
  public R join(Tuple2<GradoopId, GradoopIdSet> left, R right) throws Exception {
    right.getGraphIds().retainAll(left.f1);
    return right;
  }
}
