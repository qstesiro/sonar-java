/*
 * SonarQube Java
 * Copyright (C) 2012-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

import static org.sonar.java.CheckTestUtils.testSourcesPath;

public class DefaultPackageCheckTest {

  @Test
  public void without_package() {
    JavaCheckVerifier.newVerifier()
      .onFile(testSourcesPath("checks/EmptyFile.java"))
      .withCheck(new DefaultPackageCheck())
      .verifyIssueOnFile("Move this file to a named package.");
  }

  @Test
  public void with_package() {
    JavaCheckVerifier.newVerifier()
      .onFile(testSourcesPath("checks/WithPackage.java"))
      .withCheck(new DefaultPackageCheck())
      .verifyNoIssues();
  }

  @Test
  public void with_module() {
    JavaCheckVerifier.newVerifier()
      .onFile("src/test/files/checks/module/module-info.java")
      .withCheck(new DefaultPackageCheck())
      .verifyNoIssues();
  }

}
