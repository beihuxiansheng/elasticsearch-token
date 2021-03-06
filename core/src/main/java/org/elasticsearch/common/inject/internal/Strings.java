begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
package|;
end_package

begin_comment
comment|/**  * String utilities.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|Strings
specifier|public
class|class
name|Strings
block|{
DECL|method|Strings
specifier|private
name|Strings
parameter_list|()
block|{     }
comment|/**      * Returns a string that is equivalent to the specified string with its      * first character converted to uppercase as by {@link String#toUpperCase}.      * The returned string will have the same value as the specified string if      * its first character is non-alphabetic, if its first character is already      * uppercase, or if the specified string is of length 0.      *<p>      * For example:      *<pre>      *    capitalize("foo bar").equals("Foo bar");      *    capitalize("2b or not 2b").equals("2b or not 2b")      *    capitalize("Foo bar").equals("Foo bar");      *    capitalize("").equals("");      *</pre>      *      * @param s the string whose first character is to be uppercased      * @return a string equivalent to<tt>s</tt> with its first character      *         converted to uppercase      * @throws NullPointerException if<tt>s</tt> is null      */
DECL|method|capitalize
specifier|public
specifier|static
name|String
name|capitalize
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|s
return|;
block|}
name|char
name|first
init|=
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|char
name|capitalized
init|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|first
argument_list|)
decl_stmt|;
return|return
operator|(
name|first
operator|==
name|capitalized
operator|)
condition|?
name|s
else|:
name|capitalized
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

