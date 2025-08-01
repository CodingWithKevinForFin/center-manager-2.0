/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.slf4j.spi;

import org.slf4j.IMarkerFactory;


/**
 * An internal interface which helps the static {@link org.slf4j.MarkerFactory} 
 * class bind with the appropriate {@link IMarkerFactory} instance. 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public interface MarkerFactoryBinder {

  /**
   * Return the instance of {@link IMarkerFactory} that 
   * {@link org.slf4j.MarkerFactory} class should bind to.
   * 
   * @return the instance of {@link IMarkerFactory} that 
   * {@link org.slf4j.MarkerFactory} class should bind to.
   */
  public IMarkerFactory getMarkerFactory();

  /**
   * The String form of the {@link IMarkerFactory} object that this 
   * <code>MarkerFactoryBinder</code> instance is <em>intended</em> to return. 
   * 
   * <p>This method allows the developer to intterogate this binder's intention 
   * which may be different from the {@link IMarkerFactory} instance it is able to 
   * return. Such a discrepency should only occur in case of errors.
   * 
   * @return the class name of the intended {@link IMarkerFactory} instance
   */
  public String getMarkerFactoryClassStr();
}
