/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.model;

import com.sonar.sslr.api.Token;
import org.sonar.plugins.java.api.tree.SyntaxToken;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TreeVisitor;

import java.util.Iterator;

public class InternalSyntaxToken extends JavaTree implements SyntaxToken {

  private final Token token;

  public InternalSyntaxToken(Token token) {
    super(token.getType());
    this.token = token;
  }

  @Override
  public String text() {
    return token.getValue();
  }

  @Override
  public void accept(TreeVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getLine() {
    return token.getLine();
  }

  @Override
  public Kind getKind() {
    return Kind.TOKEN;
  }

  @Override
  public boolean isLeaf() {
    return true;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    throw new UnsupportedOperationException();
  }

}
