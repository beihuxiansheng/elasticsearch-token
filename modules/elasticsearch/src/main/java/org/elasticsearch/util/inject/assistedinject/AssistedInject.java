begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject.assistedinject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
operator|.
name|assistedinject
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
name|CONSTRUCTOR
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
comment|/**  *<p>Constructors annotated with {@code @AssistedInject} indicate that they can be instantiated by  * the {@link FactoryProvider}. Each constructor must exactly match one corresponding factory method  * within the factory interface.  *   *<p>Constructor parameters must be either supplied by the factory interface and marked with  *<code>@Assisted</code>, or they must be injectable.  *   * @deprecated {@link FactoryProvider} now works better with the standard {@literal @Inject}  *     annotation. When using that annotation, parameters are matched by name and type rather than  *     by position. In addition, values that use the standard {@literal @Inject} constructor  *     annotation are eligible for method interception.  *  * @author jmourits@google.com (Jerome Mourits)  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_annotation_defn
annotation|@
name|Target
argument_list|(
block|{
name|CONSTRUCTOR
block|}
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RUNTIME
argument_list|)
annotation|@
name|Deprecated
DECL|interface|AssistedInject
specifier|public
annotation_defn|@interface
name|AssistedInject
block|{}
end_annotation_defn

end_unit

