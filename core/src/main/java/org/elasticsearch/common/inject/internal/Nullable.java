begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * The presence of this annotation on a method parameter indicates that  * {@code null} is an acceptable value for that parameter.  It should not be  * used for parameters of primitive types.  *<p>  * This annotation may be used with the Google Web Toolkit (GWT).  *  * @author Kevin Bourrillion  */
end_comment

begin_annotation_defn
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
block|{
name|ElementType
operator|.
name|PARAMETER
block|,
name|ElementType
operator|.
name|FIELD
block|}
argument_list|)
DECL|interface|Nullable
specifier|public
annotation_defn|@interface
name|Nullable
block|{ }
end_annotation_defn

end_unit

