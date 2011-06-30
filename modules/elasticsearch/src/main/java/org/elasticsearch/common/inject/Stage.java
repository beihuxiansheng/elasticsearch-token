begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
package|;
end_package

begin_comment
comment|/**  * The stage we're running in.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_enum
DECL|enum|Stage
specifier|public
enum|enum
name|Stage
block|{
comment|/**      * We're running in a tool (an IDE plugin for example). We need binding meta data but not a      * functioning Injector. Do not inject members of instances. Do not load eager singletons. Do as      * little as possible so our tools run nice and snappy. Injectors created in this stage cannot      * be used to satisfy injections.      */
DECL|enum constant|TOOL
name|TOOL
block|,
comment|/**      * We want fast startup times at the expense of runtime performance and some up front error      * checking.      */
DECL|enum constant|DEVELOPMENT
name|DEVELOPMENT
block|,
comment|/**      * We want to catch errors as early as possible and take performance hits up front.      */
DECL|enum constant|PRODUCTION
name|PRODUCTION
block|}
end_enum

end_unit

