<template>
  <div class="settings-container">
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本设置 -->
        <el-tab-pane label="基本设置" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicForm"
            label-width="120px"
            class="settings-form"
          >
            <el-form-item label="系统名称">
              <el-input v-model="basicForm.systemName" placeholder="请输入系统名称" />
            </el-form-item>

            <el-form-item label="系统Logo">
              <el-upload
                class="logo-uploader"
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleLogoChange"
              >
                <img v-if="basicForm.logo" :src="basicForm.logo" class="logo-preview" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="版权信息">
              <el-input v-model="basicForm.copyright" placeholder="请输入版权信息" />
            </el-form-item>

            <el-form-item label="客服电话">
              <el-input v-model="basicForm.servicePhone" placeholder="请输入客服电话" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveBasic">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 用户管理 -->
        <el-tab-pane label="用户管理" name="users" v-if="isAdmin">
          <div class="user-management">
            <div class="toolbar">
              <el-button type="primary" @click="handleAddUser">
                <el-icon><Plus /></el-icon>添加用户
              </el-button>
            </div>

            <el-table :data="userList" style="width: 100%" v-loading="loading">
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="name" label="姓名" width="120" />
              <el-table-column prop="role" label="角色" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.role === 'admin' ? 'danger' : 'success'">
                    {{ row.role === 'admin' ? '管理人员' : '运营成员' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="phone" label="手机号" width="150" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                    {{ row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleEditUser(row)">
                    编辑
                  </el-button>
                  <el-button 
                    :type="row.status === 1 ? 'danger' : 'success'" 
                    size="small" 
                    @click="handleToggleStatus(row)"
                  >
                    {{ row.status === 1 ? '禁用' : '启用' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 安全设置 -->
        <el-tab-pane label="安全设置" name="security">
          <el-form
            ref="securityFormRef"
            :model="securityForm"
            label-width="180px"
            class="settings-form"
          >
            <el-form-item label="登录失败锁定次数">
              <el-input-number v-model="securityForm.loginFailLimit" :min="3" :max="10" />
            </el-form-item>

            <el-form-item label="登录失败锁定时间(分钟)">
              <el-input-number v-model="securityForm.lockTime" :min="5" :max="60" />
            </el-form-item>

            <el-form-item label="密码有效期(天)">
              <el-input-number v-model="securityForm.passwordExpireDays" :min="0" :max="365" />
              <span class="form-tip">0表示永不过期</span>
            </el-form-item>

            <el-form-item label="密码复杂度要求">
              <el-checkbox-group v-model="securityForm.passwordRules">
                <el-checkbox label="length">最少8位</el-checkbox>
                <el-checkbox label="uppercase">包含大写字母</el-checkbox>
                <el-checkbox label="lowercase">包含小写字母</el-checkbox>
                <el-checkbox label="number">包含数字</el-checkbox>
                <el-checkbox label="special">包含特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveSecurity">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 通知设置 -->
        <el-tab-pane label="通知设置" name="notification">
          <el-form
            ref="notificationFormRef"
            :model="notificationForm"
            label-width="180px"
            class="settings-form"
          >
            <el-form-item label="启用短信通知">
              <el-switch v-model="notificationForm.enableSms" />
            </el-form-item>

            <el-form-item label="启用邮件通知">
              <el-switch v-model="notificationForm.enableEmail" />
            </el-form-item>

            <el-form-item label="新用户注册通知">
              <el-switch v-model="notificationForm.notifyNewUser" />
            </el-form-item>

            <el-form-item label="紧急呼叫通知">
              <el-switch v-model="notificationForm.notifyEmergency" />
            </el-form-item>

            <el-form-item label="订单状态变更通知">
              <el-switch v-model="notificationForm.notifyOrder" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSaveNotification">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="dialogType === 'edit'" />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理人员" value="admin" />
            <el-option label="运营成员" value="operator" />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingUser" @click="handleSaveUser">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const activeTab = ref('basic')
const loading = ref(false)
const saving = ref(false)
const savingUser = ref(false)
const userDialogVisible = ref(false)
const dialogType = ref('add')

const isAdmin = computed(() => {
  return userStore.userInfo?.role === 'admin'
})

// 基本设置表单
const basicFormRef = ref(null)
const basicForm = reactive({
  systemName: '智慧养老管理平台',
  logo: '',
  copyright: '© 2024 智慧养老服务平台. All rights reserved.',
  servicePhone: '12349'
})

// 安全设置表单
const securityFormRef = ref(null)
const securityForm = reactive({
  loginFailLimit: 5,
  lockTime: 30,
  passwordExpireDays: 90,
  passwordRules: ['length', 'uppercase', 'lowercase', 'number']
})

// 通知设置表单
const notificationFormRef = ref(null)
const notificationForm = reactive({
  enableSms: true,
  enableEmail: true,
  notifyNewUser: true,
  notifyEmergency: true,
  notifyOrder: true
})

// 用户列表
const userList = ref([])

// 用户表单
const userFormRef = ref(null)
const userForm = reactive({
  id: null,
  username: '',
  name: '',
  role: 'operator',
  phone: '',
  email: '',
  password: '',
  status: 1
})

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度不能小于3位', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const loadUserList = async () => {
  loading.value = true
  try {
    // TODO: 调用获取用户列表API
    userList.value = [
      { id: 1, username: 'wxh', name: '管理员', role: 'admin', phone: '13800138000', email: 'admin@example.com', status: 1 },
      { id: 2, username: 'operator1', name: '运营人员1', role: 'operator', phone: '13800138001', email: 'operator1@example.com', status: 1 },
      { id: 3, username: 'operator2', name: '运营人员2', role: 'operator', phone: '13800138002', email: 'operator2@example.com', status: 0 }
    ]
  } catch (error) {
    console.error('加载用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSaveBasic = async () => {
  saving.value = true
  try {
    // TODO: 调用保存基本设置API
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

const handleSaveSecurity = async () => {
  saving.value = true
  try {
    // TODO: 调用保存安全设置API
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

const handleSaveNotification = async () => {
  saving.value = true
  try {
    // TODO: 调用保存通知设置API
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

const handleAddUser = () => {
  dialogType.value = 'add'
  userForm.id = null
  userForm.username = ''
  userForm.name = ''
  userForm.role = 'operator'
  userForm.phone = ''
  userForm.email = ''
  userForm.password = ''
  userForm.status = 1
  userDialogVisible.value = true
}

const handleEditUser = (row) => {
  dialogType.value = 'edit'
  Object.assign(userForm, row)
  userDialogVisible.value = true
}

const handleSaveUser = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      savingUser.value = true
      try {
        // TODO: 调用保存用户API
        ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
        userDialogVisible.value = false
        loadUserList()
      } catch (error) {
        console.error('保存失败', error)
      } finally {
        savingUser.value = false
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}该用户吗？`,
      '提示',
      { type: 'warning' }
    )
    // TODO: 调用切换状态API
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success('操作成功')
  } catch (error) {
    // 取消操作
  }
}

const handleLogoChange = (file) => {
  // TODO: 处理Logo上传
  console.log('Logo文件:', file)
}

onMounted(() => {
  loadUserList()
})
</script>

<style lang="scss" scoped>
.settings-container {
  padding: 20px;
}

.settings-card {
  min-height: 600px;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.settings-form {
  max-width: 600px;
  padding: 20px;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.user-management {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.logo-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.logo-preview {
  width: 120px;
  height: 120px;
  object-fit: contain;
}
</style>
