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
import MessageContactV1 from '@/entities/message/v1/message-contact/message-contact-v1.vue';
import SubscribeV1 from '@/entities/subscribe/v1/subscribe-v1.vue';
import Separator from '@/core/separator/separator.vue';

/**
 * Composant V1 de pied de page.
 * Affiche les liens, informations de contact, intégrer le formulaire de contact et l’abonnement.
 * Fournit des raccourcis vers les réseaux sociaux et actions rapides.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FootersV1',
  components: {
    'p-separator': Separator,
    'entities-menu': EntitiesMenu,
    'dp-title': Title,
    'banner-footers': BannerFooters,
    'message-contact-v1': MessageContactV1,
    'subscribe-v1': SubscribeV1,
  },
  setup() {
    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();
    const footersService = inject('footersService', () => new FootersService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number | null> = ref(null);
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
      } catch (err: any) {
        alertService.showHttpError(err?.response);
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

    const removeId: Ref<number | null> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IFooters) => {
      removeId.value = instance.id ?? null;
      const modal = removeEntity.value;
      if (modal && typeof modal.show === 'function') {
        modal.show();
      }
    };
    const closeDialog = () => {
      const modal = removeEntity.value;
      if (modal && typeof modal.hide === 'function') {
        modal.hide();
      }
    };
    const removeFooters = async () => {
      try {
        if (removeId.value == null) {
          return;
        }
        await footersService().delete(removeId.value);
        const message = t$('devalgasApp.footers.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveFooterss();
        closeDialog();
      } catch (error: any) {
        alertService.showHttpError(error?.response);
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
      window.location.href = MAIL_TO;
    };

    const openPhoneCall = () => {
      window.location.href = PHONE_URL;
    };

    const openSocialLink = (platform: 'linkedin' | 'twitter' | 'github' | 'medium') => {
      const socialLinks = {
        linkedin: LINKEDIN_URL,
        twitter: TWITTER_URL,
        github: GITHUB_URL,
        medium: MEDIUM_URL,
      };
      window.open(socialLinks[platform], '_blank');
    };

    const showContactDropup = ref(false);
    const toggleContactDropup = () => {
      showContactDropup.value = !showContactDropup.value;
    };
    const onContactSaved = () => {
      showContactDropup.value = false;
    };
    const openWhatsApp = () => {
      window.location.href = WHATSAPP_URL;
    };
    const separatorLabel = computed(() => t$('globalV1.separator.or').toString());

    const store = useStore();

    const authenticated = computed(() => store.authenticated);
    const writingHash = WRITING_HASH;

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
      showContactDropup,
      toggleContactDropup,
      onContactSaved,
      openWhatsApp,
      separatorLabel,
      writingHash,
    };
  },
});
