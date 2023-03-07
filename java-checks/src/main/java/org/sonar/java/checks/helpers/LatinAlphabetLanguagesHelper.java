/*
 * SonarQube Java
 * Copyright (C) 2012-2023 SonarSource SA
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
package org.sonar.java.checks.helpers;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class LatinAlphabetLanguagesHelper {

  private static final double[] WORD_FIRST_LETTER_FREQUENCIES = {2.6590433d, 1.2620203d, 1.5564419d, 0.9035879d, 0.7042806d, 1.1847413d, 0.4955763d, 0.9510469d, 1.7106735d,
    0.2684641d,
    0.2196475d, 0.7503985d, 1.087598d, 0.6504237d, 1.7798191d, 1.2322421d, 0.0468438d, 0.9674179d, 1.8778546d, 3.5174797d, 0.4200321d, 0.2624742d, 1.2915608d, 0.0095785d,
    0.1583926d, 0.0323609d};
  private static final double[] WORD_LAST_LETTER_FREQUENCIES = {0.5738782d, 0.0514104d, 0.2813366d, 2.509574d, 4.8288358d, 1.0461689d, 0.7732504d, 0.7288072d, 0.1805434d,
    0.0053191d, 0.2896774d,
    0.9860451d, 0.5045335d, 2.8780559d, 1.0164436d, 0.183349d, 0.0039651d, 1.6346942d, 3.5859066d, 1.9681061d, 0.0870041d, 0.0362143d, 0.207057d, 0.0560339d, 1.5560091d,
    0.0277812d};
  private static final double[][] CHARACTER_PAIRS_FREQUENCIES = {
    {0.0929348d, 1.3457961d, 2.7854923d, 2.5539888d, 0.2209547d, 0.6705441d, 1.8441841d, 0.2415183d, 2.1081316d, 0.1513539d, 0.7217594d, 8.0951704d, 3.0899456d, 14.1108051d,
      0.0792523d, 1.3738109d, 0.0387682d, 8.5247815d, 6.357247d, 8.850255d, 1.0730911d, 1.0915589d, 0.4722228d, 0.1239866d, 1.5762482d, 0.218569d},
    {1.9067869d, 0.1279563d, 0.0739743d, 0.0265155d, 3.4357286d, 0.0049029d, 0.005839d, 0.0312368d, 0.956677d, 0.0743913d, 0.0039669d, 1.3367944d, 0.0346314d, 0.0176708d,
      1.5688768d, 0.0101957d, 0.0006587d, 1.1051669d, 0.3112683d, 0.0468363d, 1.321757d, 0.0127821d, 0.0117435d, 0.0007594d, 1.2868484d, 0.0017814d},
    {4.0719631d, 0.0205519d, 0.4552517d, 0.0478322d, 4.0359256d, 0.0136253d, 0.0141062d, 4.2610277d, 1.9324629d, 0.0027702d, 1.1371642d, 1.4239825d, 0.0214459d, 0.0160944d,
      5.2439283d, 0.0142073d, 0.0346805d, 1.0777674d, 0.2821967d, 2.8990383d, 1.0071517d, 0.0049062d, 0.0070608d, 0.0009994d, 0.2640629d, 0.0263652d},
    {1.4407028d, 0.0366525d, 0.0727675d, 0.3063419d, 5.3874701d, 0.0432824d, 0.1899634d, 0.0710289d, 3.8700951d, 0.0319036d, 0.0064268d, 0.1858389d, 0.1456839d, 0.0833305d,
      1.2408384d, 0.0215672d, 0.0132501d, 0.6165595d, 0.7798563d, 0.0405733d, 1.0407546d, 0.1140672d, 0.1005016d, 0.0014863d, 0.2886946d, 0.0112261d},
    {4.4101634d, 0.5402931d, 3.1511903d, 8.335849d, 2.1665083d, 1.0040715d, 0.9092963d, 0.1880728d, 1.0110292d, 0.0358556d, 0.188317d, 3.8583031d, 2.2997606d, 8.5360484d,
      0.7791784d, 1.1289852d, 0.2515411d, 14.4775826d, 8.3971751d, 2.7876921d, 0.3437904d, 1.4431433d, 0.9862942d, 1.1282521d, 0.8072609d, 0.0804047d},
    {0.9463967d, 0.0087502d, 0.0314099d, 0.0487887d, 1.5768827d, 0.7325095d, 0.0113305d, 0.0027475d, 2.0741465d, 0.0032058d, 0.0049733d, 0.4309108d, 0.0223647d, 0.0049622d,
      3.2088246d, 0.0047891d, 0.0004224d, 1.5815529d, 0.0435306d, 0.5887487d, 0.4870926d, 0.0019864d, 0.0038661d, 0.0018069d, 0.0444506d, 0.0011643d},
    {1.4170025d, 0.0481224d, 0.0082768d, 0.0816686d, 3.0209535d, 0.0149393d, 0.1457067d, 1.2637355d, 1.121449d, 0.0042659d, 0.006808d, 0.5162312d, 0.0464436d, 0.4306755d,
      0.8720109d, 0.0092219d, 0.0014534d, 1.302087d, 0.3605589d, 0.1215897d, 0.9230535d, 0.003158d, 0.0317547d, 0.0021631d, 0.2001351d, 0.0043706d},
    {4.3917104d, 0.0536482d, 0.0181969d, 0.0394497d, 16.2490394d, 0.0149868d, 0.0059029d, 0.0085796d, 4.4951994d, 0.0023695d, 0.012801d, 0.1408159d, 0.1344758d, 0.270349d,
      3.0645262d, 0.0150515d, 0.0061781d, 0.5387961d, 0.2133521d, 0.6162165d, 0.5563874d, 0.0095012d, 0.0915231d, 0.0005191d, 0.3276425d, 0.0056028d},
    {3.0618075d, 0.5378794d, 5.3672662d, 1.7146727d, 2.6249932d, 0.9268649d, 1.6588638d, 0.0298823d, 0.1653869d, 0.0423041d, 0.4355309d, 3.3780001d, 1.6210354d, 16.2047698d,
      5.3259079d, 0.8335975d, 0.06607d, 2.5575842d, 7.7396368d, 6.8953537d, 0.1660788d, 1.8670839d, 0.0228681d, 0.1719965d, 0.0319517d, 0.3727173d},
    {0.5384459d, 0.0018035d, 0.0037464d, 0.0049227d, 0.3791862d, 0.0016495d, 0.0030904d, 0.0047405d, 0.1010139d, 0.0030284d, 0.0051238d, 0.0023837d, 0.0052344d, 0.0054429d,
      0.5759392d, 0.0419588d, 0.0001699d, 0.0199578d, 0.0076412d, 0.0024995d, 0.511387d, 0.0018288d, 0.0015663d, 0.0003011d, 0.0024199d, 0.0015624d},
    {0.4584126d, 0.0151268d, 0.0066457d, 0.0105469d, 1.4778732d, 0.0156612d, 0.0235217d, 0.0926333d, 0.9247234d, 0.0043772d, 0.028332d, 0.1271601d, 0.0365927d, 0.2679984d,
      0.2570398d, 0.0171023d, 0.0008536d, 0.0805852d, 0.6105992d, 0.0285942d, 0.115693d, 0.007477d, 0.0280535d, 0.0013893d, 0.1124803d, 0.0013102d},
    {4.548621d, 0.2915819d, 0.089763d, 1.4893018d, 6.3515348d, 0.2311593d, 0.0921435d, 0.0322694d, 5.0245197d, 0.0092016d, 0.1856222d, 3.8105199d, 0.4655274d, 0.0369741d,
      2.6739741d, 0.153297d, 0.0018632d, 0.0689171d, 1.1608595d, 0.8001751d, 0.9675034d, 0.2130048d, 0.102301d, 0.0016705d, 2.2223913d, 0.0112197d},
    {4.4732575d, 0.9971554d, 0.0892385d, 0.0154109d, 5.1403143d, 0.0175541d, 0.0107624d, 0.0132742d, 2.3275322d, 0.0027971d, 0.0088211d, 0.0391983d, 0.8021489d, 0.0887556d,
      2.0619624d, 1.4747935d, 0.0008712d, 0.0401555d, 0.5991706d, 0.0220131d, 0.881698d, 0.0076061d, 0.0126008d, 0.0026432d, 0.3033358d, 0.0023306d},
    {3.6077607d, 0.0975166d, 2.589403d, 7.9063088d, 4.6470058d, 0.3556239d, 6.2425962d, 0.1073503d, 3.0557149d, 0.0788384d, 0.6186266d, 0.3370293d, 0.17844d, 0.8651743d,
      2.8184623d, 0.0480307d, 0.0223127d, 0.102047d, 3.1470283d, 6.0322931d, 0.6582686d, 0.2595533d, 0.0651263d, 0.0103653d, 0.6055506d, 0.0792284d},
    {0.5652844d, 0.5958594d, 1.2994182d, 1.1115993d, 0.2776038d, 6.3941577d, 0.7715881d, 0.2678748d, 0.5253778d, 0.0947828d, 0.4635273d, 2.920816d, 3.7278997d, 11.6017554d,
      1.4753292d, 1.8177215d, 0.0118906d, 8.8419322d, 1.7595924d, 2.4753308d, 4.5261994d, 1.4178813d, 1.8557399d, 0.1644457d, 0.2754933d, 0.0487071d},
    {2.5624505d, 0.0202675d, 0.0204994d, 0.022034d, 2.9595845d, 0.015402d, 0.0525791d, 0.7732213d, 1.0678211d, 0.0022877d, 0.0096737d, 1.8234707d, 0.0814874d, 0.0208135d,
      2.2662701d, 0.7738026d, 0.0008108d, 2.6371155d, 0.3885394d, 0.5976453d, 0.7912125d, 0.0032943d, 0.0076619d, 0.0009594d, 0.0841922d, 0.0031912d},
    {0.0116363d, 0.0014528d, 0.00085d, 0.0004046d, 0.0011631d, 0.0009863d, 0.0001834d, 0.0003922d, 0.0148139d, 0.0001364d, 0.0001327d, 0.0016952d, 0.0008495d, 0.0003971d,
      0.0012619d, 0.0004823d, 0.0007202d, 0.0008395d, 0.0016322d, 0.0009446d, 0.781532d, 0.0008461d, 0.0007344d, 0.0002711d, 0.0001503d, 0.0001024d},
    {5.2664628d, 0.2722724d, 1.0315216d, 1.5041745d, 11.4452395d, 0.2187558d, 0.8034347d, 0.1197433d, 5.9004085d, 0.0136782d, 0.7869179d, 0.7888557d, 1.1905663d, 1.5408916d,
      5.2636683d, 0.2840431d, 0.0162732d, 0.8714239d, 3.073065d, 3.0935018d, 1.0043785d, 0.4845634d, 0.1116324d, 0.0059307d, 1.9104828d, 0.0298333d},
    {1.3958189d, 0.1084548d, 1.3997172d, 0.0477502d, 5.5977636d, 0.0961754d, 0.0285751d, 2.4672552d, 3.9516833d, 0.0077479d, 0.339737d, 0.4491616d, 0.3986528d, 0.1207062d,
      2.8901855d, 1.1973049d, 0.080366d, 0.0717654d, 2.3862707d, 7.8045342d, 1.7449085d, 0.0408148d, 0.1931512d, 0.0020589d, 0.350114d, 0.0198438d},
    {4.0046636d, 0.2315084d, 0.6360105d, 0.0243227d, 8.9176277d, 0.0526605d, 0.0200005d, 18.3086482d, 7.9891653d, 0.0062372d, 0.0151697d, 0.6619201d, 0.1460394d, 0.0806787d,
      6.2468781d, 0.0274042d, 0.0007526d, 2.9306096d, 2.1799955d, 1.1239766d, 1.616373d, 0.1039086d, 0.5300224d, 0.003287d, 1.5511418d, 0.0753694d},
    {1.0405339d, 0.7757457d, 1.0559389d, 0.6735502d, 1.0636866d, 0.1190306d, 0.7986363d, 0.0290528d, 0.7019287d, 0.0192097d, 0.1602448d, 1.8289351d, 1.1563737d, 3.2297811d,
      0.0582932d, 0.8142665d, 0.0061515d, 3.2935746d, 3.1995976d, 2.5388821d, 0.0101856d, 0.0400647d, 0.0116986d, 0.0440218d, 0.0426452d, 0.0412904d},
    {0.9870774d, 0.0037721d, 0.006448d, 0.017297d, 4.6552622d, 0.0133728d, 0.0130077d, 0.0038715d, 2.1772529d, 0.0008623d, 0.0029271d, 0.0134182d, 0.0028136d, 0.0068546d,
      0.5066435d, 0.0041575d, 0.0002258d, 0.0206034d, 0.0273123d, 0.0044648d, 0.0203117d, 0.002107d, 0.0015298d, 0.0005471d, 0.0621332d, 0.0006573d},
    {2.9790214d, 0.0193429d, 0.0157836d, 0.018724d, 1.8652788d, 0.0161439d, 0.004503d, 1.5224859d, 2.2852698d, 0.0017198d, 0.0207637d, 0.0770983d, 0.0136184d, 0.6167433d,
      1.3048155d, 0.018365d, 0.0009061d, 0.2921267d, 0.250538d, 0.0269223d, 0.0164429d, 0.0029288d, 0.0208577d, 0.0010678d, 0.0402552d, 0.0011097d},
    {0.1665908d, 0.0055061d, 0.086935d, 0.0017397d, 0.1209231d, 0.0230238d, 0.0004892d, 0.0240804d, 0.2232723d, 0.0004185d, 0.0003742d, 0.006829d, 0.0051386d, 0.0011599d,
      0.0317656d, 0.2459899d, 0.0005172d, 0.0012081d, 0.0033771d, 0.3847311d, 0.0254768d, 0.0063257d, 0.0036148d, 0.0100832d, 0.0172085d, 0.0002967d},
    {0.3162593d, 0.054578d, 0.1219273d, 0.0766018d, 0.7890763d, 0.0112568d, 0.021848d, 0.0089087d, 0.1617439d, 0.0032816d, 0.0146214d, 0.2275842d, 0.1906941d, 0.1630151d,
      0.6414934d, 0.1588797d, 0.0004128d, 0.1039721d, 0.4814257d, 0.1023695d, 0.0593967d, 0.0103587d, 0.0387865d, 0.0029518d, 0.005733d, 0.0142095d},
    {0.2393676d, 0.0081167d, 0.0035083d, 0.0052527d, 0.3775166d, 0.0012409d, 0.0050274d, 0.0330923d, 0.1787957d, 0.0006392d, 0.006042d, 0.0129057d, 0.0072316d, 0.0071939d,
      0.1036998d, 0.0025679d, 0.0009882d, 0.0047487d, 0.0052224d, 0.005546d, 0.0411031d, 0.0031899d, 0.0047273d, 0.0007615d, 0.0223349d, 0.0495398d},
  };

  private static final Pattern wordPattern = Pattern.compile("[a-z]++|[A-Z][a-z]++|[A-Za-z]++");

  private LatinAlphabetLanguagesHelper() {
    // utility class
  }

  /**
   * @param text to analyze
   * @return 1.0 in average for random string, and above if it looks like language (ex: could be compared using > 1.5d)
   */
  public static double humanLanguageScore(String text) {
    text = StringUtils.stripAccents(text);
    double totalScore = 0.0d;
    double totalWeight = 0.0d;
    int camelCaseSeparator = 0;
    int lastFindingEnd = -1;
    int allWorldLength = 0;
    Matcher matcher = wordPattern.matcher(text);
    while (matcher.find()) {
      if (matcher.start() == lastFindingEnd) {
        camelCaseSeparator++;
      }
      String word = matcher.group().toLowerCase(Locale.ROOT);
      allWorldLength += word.length();
      double weight = word.length();
      totalScore += wordScore(word) * weight;
      totalWeight += weight;
      lastFindingEnd = matcher.end();
    }
    if (totalWeight == 0.0d) {
      return 1.0d;
    }
    int separatorCount = text.length() - allWorldLength + camelCaseSeparator;
    int expectedSeparatorCount = text.length() / 6;
    int unexpectedSeparatorCount = Math.abs(separatorCount - expectedSeparatorCount);
    return (((totalScore / totalWeight) * allWorldLength) + (0.1d * unexpectedSeparatorCount)) / (allWorldLength + unexpectedSeparatorCount);
  }

  private static int index(char ch) {
    return ch - 'a';
  }

  private static double wordScore(String word) {
    if (word.length() == 1) {
      // returning the one letter statics gave a too high score to 'a',
      // so we decided to just always return a constant
      return 0.1d;
    }
    char firstChar = word.charAt(0);
    char lastChar = word.charAt(word.length() - 1);
    double firstCharScore = WORD_FIRST_LETTER_FREQUENCIES[index(firstChar)];
    double lastCharScore = WORD_LAST_LETTER_FREQUENCIES[index(lastChar)];
    double pairsScore = 0.0d;
    int numberOfPairs = word.length() - 1;
    for (int i = 0; i < numberOfPairs; i++) {
      pairsScore += CHARACTER_PAIRS_FREQUENCIES[index(word.charAt(i))][index(word.charAt(i + 1))];
    }
    return (firstCharScore + lastCharScore + pairsScore) / (numberOfPairs + 2);
  }

}
