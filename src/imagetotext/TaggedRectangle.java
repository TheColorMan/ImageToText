/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kevin Prehn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package imagetotext;

import java.awt.Rectangle;

/**
 * A rectangle with a keyword tag on it.
 * @author Kevin Prehn
 */
public class TaggedRectangle extends Rectangle {

    private String tag;
    
    public TaggedRectangle(String tag, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
    
    public void setWidth(int w) {
        setSize(w, (int)getHeight());
    }
    
    public void setHeight(int h) {
        setSize((int)getWidth(), h);
    }
    
    public void setX(int x) {
        setLocation(x, (int)getY());
    }
    
    public void setY(int y) {
        setLocation((int)getX(), y);
    }
    
    /**
     * Returns if another tagged rectangle shares the same tag
     * @param other The other rectangle's tag
     * @return Whether or not they share the same tag.
     */
    public boolean hasSameTag(TaggedRectangle other) {
        return other.getTag().equals(tag);
    }
}
