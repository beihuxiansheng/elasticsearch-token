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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Interface for Objects which can be inserted into a TLinkedList.  *<p/>  *<p>  * Created: Sat Nov 10 15:23:41 2001  *</p>  *  * @author Eric D. Friedman  * @version $Id: TLinkable.java,v 1.2 2001/12/03 00:16:25 ericdf Exp $  * @see org.elasticsearch.util.gnu.trove.TLinkedList  */
end_comment

begin_interface
DECL|interface|TLinkable
specifier|public
interface|interface
name|TLinkable
extends|extends
name|Serializable
block|{
comment|/**      * Returns the linked list node after this one.      *      * @return a<code>TLinkable</code> value      */
DECL|method|getNext
specifier|public
name|TLinkable
name|getNext
parameter_list|()
function_decl|;
comment|/**      * Returns the linked list node before this one.      *      * @return a<code>TLinkable</code> value      */
DECL|method|getPrevious
specifier|public
name|TLinkable
name|getPrevious
parameter_list|()
function_decl|;
comment|/**      * Sets the linked list node after this one.      *      * @param linkable a<code>TLinkable</code> value      */
DECL|method|setNext
specifier|public
name|void
name|setNext
parameter_list|(
name|TLinkable
name|linkable
parameter_list|)
function_decl|;
comment|/**      * Sets the linked list node before this one.      *      * @param linkable a<code>TLinkable</code> value      */
DECL|method|setPrevious
specifier|public
name|void
name|setPrevious
parameter_list|(
name|TLinkable
name|linkable
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// TLinkable
end_comment

end_unit

