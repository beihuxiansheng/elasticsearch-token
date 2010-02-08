begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * ThreadSafe  *<p/>  * The class to which this annotation is applied is thread-safe. This means that  * no sequences of accesses (reads and writes to public fields, calls to public  * methods) may put the object into an invalid state, regardless of the  * interleaving of those actions by the runtime, and without requiring any  * additional synchronization or coordination on the part of the caller.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_annotation_defn
annotation|@
name|Documented
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|TYPE
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|CLASS
argument_list|)
DECL|interface|ThreadSafe
specifier|public
annotation_defn|@interface
name|ThreadSafe
block|{ }
end_annotation_defn

end_unit

