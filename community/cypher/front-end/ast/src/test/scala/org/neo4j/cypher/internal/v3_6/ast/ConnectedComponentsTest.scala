/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
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
package org.neo4j.cypher.internal.v3_6.ast

import org.neo4j.cypher.internal.v3_6.expressions.LogicalVariable
import org.neo4j.cypher.internal.v3_6.util.test_helpers.CypherFunSuite
import org.neo4j.cypher.internal.v3_6.expressions.Variable

class ConnectedComponentsTest extends CypherFunSuite {
  import connectedComponents._

  test("(a)->(b), (c)->(d) has two connected components") {
    val disconnected = connectedComponents(Vector(
      ComponentPart(varFor("a"), varFor("b")),
      ComponentPart(varFor("c"), varFor("d"))))

    disconnected should equal(Vector(
      ConnectedComponent(ComponentPart(varFor("a"), varFor("b"))),
        ConnectedComponent(ComponentPart(varFor("c"), varFor("d")))
      ))
  }

  test("(a)->(b)->(c) does contain one connected component") {
    val disconnected = connectedComponents(Vector(
      ComponentPart(varFor("a"), varFor("b")),
      ComponentPart(varFor("b"), varFor("c"))))

    disconnected should equal(Vector(
      ConnectedComponent(ComponentPart(varFor("a"), varFor("b")),
        ComponentPart(varFor("b"), varFor("c")))))
  }

  test("(a)->(b)->(c)->(d) does only contain one component") {
    val disconnected = connectedComponents(Vector(
      ComponentPart(varFor("a"), varFor("b")),
      ComponentPart(varFor("b"), varFor("c")),
      ComponentPart(varFor("c"), varFor("d"))
    ))

    disconnected shouldBe Vector(ConnectedComponent(
      ComponentPart(varFor("a"), varFor("b")),
      ComponentPart(varFor("b"), varFor("c")),
      ComponentPart(varFor("c"), varFor("d")))
    )
  }

  test("(a)->(b)->(c)-(a) contains one component ") {
    val disconnected = connectedComponents(Vector
    (
      ComponentPart(varFor("a"), varFor("b")),
      ComponentPart(varFor("b"), varFor("c")),
      ComponentPart(varFor("c"), varFor("a"))
    ))

    disconnected shouldBe Vector(ConnectedComponent(
      ComponentPart(varFor("a"), varFor("b")),
      ComponentPart(varFor("b"), varFor("c")),
      ComponentPart(varFor("c"), varFor("a"))
    ))
  }

  private def varFor(name: String): LogicalVariable = Variable(name)(null)
}
