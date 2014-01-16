/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiler.v2_0.ast

import org.neo4j.cypher.internal.compiler.v2_0._
import org.neo4j.cypher.internal.compiler.v2_0.commands

case class OrderBy(sortItems: Seq[SortItem])(val token: InputToken) extends AstNode with SemanticCheckable {
  def semanticCheck = sortItems.semanticCheck
}

sealed trait SortItem extends AstNode with SemanticCheckable {
  def expression: Expression
  def semanticCheck = expression.semanticCheck(Expression.SemanticContext.Results)

  def toCommand: commands.SortItem
}
case class AscSortItem(expression: Expression)(val token: InputToken) extends SortItem {
  def toCommand = commands.SortItem(expression.toCommand, ascending = true)
}
case class DescSortItem(expression: Expression)(val token: InputToken) extends SortItem {
  def toCommand = commands.SortItem(expression.toCommand, ascending = false)
}
