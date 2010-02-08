begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_comment
comment|/**  * This object hashing strategy uses the System.identityHashCode  * method to provide identity hash codes.  These are identical to the  * value produced by Object.hashCode(), even when the type of the  * object being hashed overrides that method.  *<p/>  * Created: Sat Aug 17 11:13:15 2002  *  * @author Eric Friedman  * @version $Id: TObjectIdentityHashingStrategy.java,v 1.4 2007/06/11 15:26:44 robeden Exp $  */
end_comment

begin_class
DECL|class|TObjectIdentityHashingStrategy
specifier|public
specifier|final
class|class
name|TObjectIdentityHashingStrategy
parameter_list|<
name|T
parameter_list|>
implements|implements
name|TObjectHashingStrategy
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Delegates hash code computation to the System.identityHashCode(Object) method.      *      * @param object for which the hashcode is to be computed      * @return the hashCode      */
DECL|method|computeHashCode
specifier|public
specifier|final
name|int
name|computeHashCode
parameter_list|(
name|T
name|object
parameter_list|)
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|object
argument_list|)
return|;
block|}
comment|/**      * Compares object references for equality.      *      * @param o1 an<code>Object</code> value      * @param o2 an<code>Object</code> value      * @return true if o1 == o2      */
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|T
name|o1
parameter_list|,
name|T
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|==
name|o2
return|;
block|}
block|}
end_class

begin_comment
comment|// TObjectIdentityHashingStrategy
end_comment

end_unit

