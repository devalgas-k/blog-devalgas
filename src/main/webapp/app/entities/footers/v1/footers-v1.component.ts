import { type Ref, defineComponent, inject, onMounted, ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n';

import FootersService from '../footers.service';
import { type IFooters } from '@/shared/model/footers.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';
import EntitiesMenu from '@/entities/entities-menu.vue';
import Title from '@/core/title/title.vue';
import { useRouter } from 'vue-router';
import { useStore } from '@/store.ts';
import BannerFooters from '@/core/banner/banner-footers.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FootersV1',
  components: {
    'entities-menu': EntitiesMenu,
    'dp-title': Title,
    'banner-footers': BannerFooters,
  },
  setup() {
    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();
    const footersService = inject('footersService', () => new FootersService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);

    const footers: Ref<IFooters[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
    };

    const sort = (): Array<any> => {
      const result = [`${propOrder.value},${reverse.value ? 'desc' : 'asc'}`];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    const retrieveFooterss = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = await footersService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        footers.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveFooterss();
    };

    onMounted(async () => {
      await retrieveFooterss();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IFooters) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeFooters = async () => {
      try {
        await footersService().delete(removeId.value);
        const message = t$('devalgasApp.footers.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveFooterss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        // first page, retrieve new data
        await retrieveFooterss();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveFooterss();
    });

    const router = useRouter();

    const navigateTo = (path: string) => {
      router.push(path);
    };

    const openMailTo = () => {
      window.location.href = 'mailto:kamgadevalgas@icloud.com';
    };

    const openPhoneCall = () => {
      window.location.href = 'tel:+23055040199';
    };

    const openSocialLink = (platform: string) => {
      const socialLinks = {
        linkedin: 'https://www.linkedin.com/in/devalgas',
        twitter: 'https://twitter.com/devalgas',
        github: 'https://github.com/devalgas',
        medium: 'https://medium.com/@devalgas',
      };
      window.open(socialLinks[platform], '_blank');
    };

    const store = useStore();

    const authenticated = computed(() => store.authenticated);

    return {
      footers,
      handleSyncList,
      isFetching,
      retrieveFooterss,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeFooters,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
      ...dataUtils,

      authenticated,
      navigateTo,
      openMailTo,
      openPhoneCall,
      openSocialLink,
    };
  },
});
