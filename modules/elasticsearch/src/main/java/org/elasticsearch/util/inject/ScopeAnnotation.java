begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|ANNOTATION_TYPE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|RUNTIME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * Annotates annotations which are used for scoping. Only one such annotation  * may apply to a single implementation class. You must also annotate scope  * annotations with {@code @Retention(RUNTIME)}. For example:  *  *<pre>  *   {@code @}Retention(RUNTIME)  *   {@code @}Target(TYPE)  *   {@code @}ScopeAnnotation  *   public {@code @}interface SessionScoped {}  *</pre>  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
name|ANNOTATION_TYPE
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RUNTIME
argument_list|)
DECL|interface|ScopeAnnotation
specifier|public
annotation_defn|@interface
name|ScopeAnnotation
block|{}
end_annotation_defn

end_unit

