# Rounded Corners Background Span
<a href="/images/1_left.png" title="Click to see in full size"><img src="/images/1_left.png" width="250" alt="Left Aligned" /></a>&nbsp;
<a href="/images/2_center.png" title="Click to see in full size"><img src="/images/2_center.png" width="250" alt="Center Aligned" /></a>&nbsp;
<a href="/images/3_right.png" title="Click to see in full size"><img src="/images/3_right.png" width="250" alt="Right Aligned" /></a>


## Setup and usage

**Fork this project**. Gradle dependency is **outdated**.

You can create spannable string using  **RoundedCornersBackgroundSpan.Builder**. All methods have JavaDoc so check it to understand what each method is doing.

```JAVA
final Spannable spanned = new RoundedCornersBackgroundSpan.Builder(this)
    .setTextPadding(float)
    .setCornersRadius(float)
    .setTextPaddingRes(dimenId)
    .setCornersRadiusRes(dimenId)
    .setSpacingSize(float)
    .setSpacingSizeRes(dimenId)
    .setTextAlignment(alignment)
    .addTextPart(string, color)
    .addTextPart(stringId, colorId)
    .addTextPart(string)
    .addTextPart(stringId)
    .build()
textView.setText(spanned);
```

As text parts you can pass any `CharSequence`, even another `Spanned` strings with their own spans!

## Restrictions:
* Text with mixed layout directions is not supported (i.e. latin and arabic).
* You must always apply padding, lines spacing and shadow to your TextView. See sample app for example.
* Shadow radius must be equal to text padding.
* Prefer transparent colors for shadow. You can set shadow via code `TextView.setShadowLayer(padding, 0, 0, Color.TRANSPARENT);`. 
* You can't set transparent shadow color via XML. Use non-zero colors, for example, `#00000001`.

<br />

## Changelog

| Version | Changes                         |
| --- | --- |
| v.1.0.0 | First public release            |

<br />

## Support

You can support this library by creating a pull request with bug fixes and/or new features on `develop` branch. Any pull requests on `master` branch will be removed. 

<br />

## License
    The MIT License (MIT)
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
