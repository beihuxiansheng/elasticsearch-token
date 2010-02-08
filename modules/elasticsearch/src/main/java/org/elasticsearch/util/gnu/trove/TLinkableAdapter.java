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
comment|/**  * Adapter for TLinkable interface which implements the interface and can  * therefore be extended trivially to create TLinkable objects without  * having to implement the obvious.  *<p/>  *<p>  * Created: Thurs Nov 15 16:25:00 2001  *</p>  *  * @author Jason Baldridge  * @version $Id: TLinkableAdapter.java,v 1.1 2006/11/10 23:27:56 robeden Exp $  * @see org.elasticsearch.util.gnu.trove.TLinkedList  */
end_comment

begin_class
DECL|class|TLinkableAdapter
specifier|public
class|class
name|TLinkableAdapter
implements|implements
name|TLinkable
block|{
DECL|field|_previous
DECL|field|_next
name|TLinkable
name|_previous
decl_stmt|,
name|_next
decl_stmt|;
comment|/**      * Returns the linked list node after this one.      *      * @return a<code>TLinkable</code> value      */
DECL|method|getNext
specifier|public
name|TLinkable
name|getNext
parameter_list|()
block|{
return|return
name|_next
return|;
block|}
comment|/**      * Returns the linked list node before this one.      *      * @return a<code>TLinkable</code> value      */
DECL|method|getPrevious
specifier|public
name|TLinkable
name|getPrevious
parameter_list|()
block|{
return|return
name|_previous
return|;
block|}
comment|/**      * Sets the linked list node after this one.      *      * @param linkable a<code>TLinkable</code> value      */
DECL|method|setNext
specifier|public
name|void
name|setNext
parameter_list|(
name|TLinkable
name|linkable
parameter_list|)
block|{
name|_next
operator|=
name|linkable
expr_stmt|;
block|}
comment|/**      * Sets the linked list node before this one.      *      * @param linkable a<code>TLinkable</code> value      */
DECL|method|setPrevious
specifier|public
name|void
name|setPrevious
parameter_list|(
name|TLinkable
name|linkable
parameter_list|)
block|{
name|_previous
operator|=
name|linkable
expr_stmt|;
block|}
block|}
end_class

end_unit

