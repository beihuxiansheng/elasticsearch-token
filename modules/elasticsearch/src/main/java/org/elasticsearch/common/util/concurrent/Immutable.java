begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
comment|/**  * Immutable  *<p/>  * The class to which this annotation is applied is immutable. This means that  * its state cannot be seen to change by callers. Of necessity this means that  * all public fields are final, and that all public final reference fields refer  * to other immutable objects, and that methods do not publish references to any  * internal state which is mutable by implementation even if not by design.  * Immutable objects may still have internal mutable state for purposes of  * performance optimization; some state variables may be lazily computed, so  * long as they are computed from immutable state and that callers cannot tell  * the difference.  *<p/>  * Immutable objects are inherently thread-safe; they may be passed between  * threads or published without synchronization.  *  * @author kimchy (Shay Banon)  */
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
DECL|interface|Immutable
specifier|public
annotation_defn|@interface
name|Immutable
block|{ }
end_annotation_defn

end_unit

